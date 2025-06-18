package intelligentcurtainwall.modelingcommunication.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseMessage<T> {
    private Integer code;
    private String message;
    private T data;

    public ResponseMessage(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     *
     * @param data 相应数据
     * @return ResponseMessage
     */
    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<>(HttpStatus.OK.value(), "success", data);
    }

    /**
     * 成功相应（自定义消息）
     *
     * @param data 响应数据
     * @param message 自定义成功消息
     * @return ResponseMessage
     */
    public static <T> ResponseMessage<T> success(T data, String message) {
        return new ResponseMessage<>(HttpStatus.OK.value(), message, data);
    }

    /**
     * 失败响应（默认400错误码）
     *
     * @param message 错误消息
     * @return ResponseMessage
     */
    public static <T> ResponseMessage<T> fail(String message) {
        return new ResponseMessage<>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    /**
     * 失败响应(自定义错误码)
     *
     * @param code 错误码
     * @param message 错误消息
     * @return ResponseMessage
     */
    public static <T> ResponseMessage<T> fail(Integer code, String message) {
        return new ResponseMessage<>(code, message, null);
    }

    /**
     * 失败响应(使用HttpStatus)
     * @param status Http状态码
     * @param message 错误消息
     * @return ResponseMessage
     */
    public static <T> ResponseMessage<T> fail(HttpStatus status, String message) {
        return new ResponseMessage<>(status.value(), message, null);
    }
}

