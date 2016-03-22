
package com.dv.persistnote.base.network.bean;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Result {

    @SerializedName("errNum")
    @Expose
    private Integer errNum;
    @SerializedName("errMsg")
    @Expose
    private String errMsg;
    @SerializedName("retData")
    @Expose
    private RetData retData;

    /**
     * 
     * @return
     *     The errNum
     */
    public Integer getErrNum() {
        return errNum;
    }

    /**
     * 
     * @param errNum
     *     The errNum
     */
    public void setErrNum(Integer errNum) {
        this.errNum = errNum;
    }

    /**
     * 
     * @return
     *     The errMsg
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * 
     * @param errMsg
     *     The errMsg
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    /**
     * 
     * @return
     *     The retData
     */
    public RetData getRetData() {
        return retData;
    }

    /**
     * 
     * @param retData
     *     The retData
     */
    public void setRetData(RetData retData) {
        this.retData = retData;
    }

}
