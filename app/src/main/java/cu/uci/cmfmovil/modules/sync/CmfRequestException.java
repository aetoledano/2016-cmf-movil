package cu.uci.cmfmovil.modules.sync;

/**
 * Created by raven on 6/9/16.
 */
public class CmfRequestException extends Exception {

    int statusCode;

    public CmfRequestException(String detailMessage, int statusCode) {
        super(detailMessage);
        this.statusCode = statusCode;
    }
}
