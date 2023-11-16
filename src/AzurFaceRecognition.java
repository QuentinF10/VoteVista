import com.microsoft.azure.cognitiveservices.vision.faceapi.FaceAPI;
import com.microsoft.azure.cognitiveservices.vision.faceapi.FaceAPIManager;

public class AzurFaceRecognition {

    private final FaceAPI client;
    
    public AzurFaceRecognition(FaceAPI client) {
        this.client = client;
    }
}
