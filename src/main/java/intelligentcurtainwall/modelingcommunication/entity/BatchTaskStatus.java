package intelligentcurtainwall.modelingcommunication.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchTaskStatus {

    private int total;
    private int processed;
    private boolean completed;
    private List<String> failedPaths = new ArrayList<>();

}
