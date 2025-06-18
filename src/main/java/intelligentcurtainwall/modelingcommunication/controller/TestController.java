package intelligentcurtainwall.modelingcommunication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test-2024-11-21")
    public String test() {
        return "智慧幕墙--无人机采集数据的3D建模与通讯系统";
    }
}