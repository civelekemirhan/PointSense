package com.wexec.pointsense.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Map;

/**
 * ONNX model yükleyici ve çıkarım servisi.
 *
 * Model dosyası bulunamazsa heuristik (hedefe doğru git) moduna geçer.
 *
 * Beklenen model formatı:
 *   Girdi  : float32[1, 12]  — normalize edilmiş: agent_xy, target_xy, sensor x8
 *   Çıktı  : float32[1,  4]  — Q-değerleri: 0=Sol, 1=Sağ, 2=Yukarı, 3=Aşağı
 *
 * Dönüşüm komutları:
 *   Keras  → tf2onnx:   python -m tf2onnx.convert --saved-model model --output model.onnx
 *   PyTorch:            torch.onnx.export(model, dummy, "model.onnx", ...)
 */
@Service
public class ModelInferenceService {

    private static final Logger log = LoggerFactory.getLogger(ModelInferenceService.class);
    public static final String[] ACTION_LABELS = {"LEFT", "RIGHT", "UP", "DOWN"};

    @Value("${rl.model.path:model.onnx}")
    private String modelPath;

    private OrtEnvironment env;
    private OrtSession session;
    private boolean modelLoaded = false;

    @PostConstruct
    public void init() {
        File f = new File(modelPath);
        if (!f.exists()) {
            log.warn("Model dosyası bulunamadı: {} — heuristik mod aktif.", f.getAbsolutePath());
            return;
        }
        try {
            env = OrtEnvironment.getEnvironment();
            session = env.createSession(f.getAbsolutePath());
            modelLoaded = true;
            log.info("ONNX model yüklendi: {}", f.getAbsolutePath());
        } catch (OrtException e) {
            log.error("Model yüklenemedi, heuristik mod aktif: {}", e.getMessage());
        }
    }

    /** @param input Normalizer'dan çıkan 12 elemanlı float dizisi */
    public InferenceResult infer(float[] input) {
        return modelLoaded ? inferOnnx(input) : inferHeuristic(input);
    }

    // ---- ONNX çıkarımı ----

    private InferenceResult inferOnnx(float[] input) {
        try {
            String inputName = session.getInputNames().iterator().next();
            long[] shape = {1L, input.length};
            OnnxTensor tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(input), shape);
            try (OrtSession.Result result = session.run(Map.of(inputName, tensor))) {
                float[][] output = (float[][]) result.get(0).getValue();
                float[] q = output[0];
                return new InferenceResult(argmax(q), q);
            }
        } catch (OrtException e) {
            log.error("ONNX çıkarım hatası, heuristik'e geçildi: {}", e.getMessage());
            return inferHeuristic(input);
        }
    }

    // ---- Heuristik fallback ----
    // Normalize girdi: [0]=agent_x, [1]=agent_y, [2]=target_x, [3]=target_y
    private InferenceResult inferHeuristic(float[] input) {
        float dx = input[2] - input[0];
        float dy = input[3] - input[1];
        float[] q = { -dx, dx, -dy, dy };   // SOL, SAĞ, YUKARI, AŞAĞI
        return new InferenceResult(argmax(q), q);
    }

    private int argmax(float[] arr) {
        int best = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[best]) best = i;
        }
        return best;
    }

    public record InferenceResult(int action, float[] qValues) {
        public String actionLabel() {
            return ACTION_LABELS[action];
        }
    }
}
