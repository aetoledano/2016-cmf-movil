package cu.uci.cmfmovil.modules.sync.status;

/**
 * Created by cosito on 6/8/16.
 */
public class StatusCode {

    /*
    Taken from https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html Jun 8, 2016
     */

    /*
    List of HTTP status code
     */
    public static final int STATUS_CODE_OK = 200;

    public static final int STATUS_CODE_CREATED = 201;

    public static final int STATUS_CODE_BAD_REQUEST = 400;

    public static final int STATUS_CODE_UNAUTHORIZED = 401;

    public static final int STATUS_CODE_FORBIDDEN = 403;

    public static final int STATUS_CODE_NOT_FOUND = 404;

    /*
    List of request build status code
    */
    public static final int STATUS_CODE_NOT_USER_OR_PASSWORD = 1000;



}
