package com.dv.persistnote.framework.core;

public class Notification {
	public int id;
	public Object extObj;
	
	public Notification(int id, Object extObj) {
		this.id = id;
		this.extObj = extObj;
	}
	
	public Notification(int id) {
		this(id, null);
	}
	
    public static Notification obtain(int id, Object extObj) {
        return new Notification(id, extObj);
    }

    public static Notification obtain(int id) {
        return new Notification(id);
    }
    
    /**
     * 根据所给的{@code Notification}生成一份一样的拷贝
     * @param orig
     *          原{@code Notification}实例
     * @return  新的实例，但是内容和给定的实例一致
     */
    public static Notification obtain(Notification orig) {
        Notification notification = new Notification(orig.id, orig.extObj);
        return notification;
    }

    @Override
    public String toString() {
        return "Notification [id=" + id + ", extObj=" + extObj + "]";
    }

}