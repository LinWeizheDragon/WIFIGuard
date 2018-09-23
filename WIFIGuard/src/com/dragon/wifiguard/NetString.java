package com.dragon.wifiguard;

/**
 * 网络数据大小类
 * @author 林炜哲
 *
 */
public class NetString{
	private long data=0;
	public void setData(long data){
		this.data=data;
	}
	public long getData(){
		return data;
	}
	public NetString combineData(NetString a,NetString b){
		NetString c=new NetString();
		c.setData(a.getData()+b.getData());
		return c;
	}
	public String toString(){
		return DealData(data);
	}
	private String DealData(long data){
		String NewString = "无数据";
		if (data == -1) {
			return NewString;
		}
		if (data < 1024 && data >= 0)
			NewString = data + "B";
		if (data >= 1024 && data <= 1048576)
			NewString = Math.round((data / 1024 * 10000) / 10000.0) + "KB";
		if (data >= 1048576 && data <= 1073741824)
			NewString = Math.round((data / 1024 / 1024 * 10000) / 10000.0)
					+ "MB";
		if (data >= 1073741824)
		{
			long x=(long)Math.floor((data / 1024 / 1024 / 1024 * 10000) / 10000.0);
			NewString = x+ "GB";
			long b=data-x*1024*1024*1024;
			NewString = NewString+DealData(b);
		}
		return NewString;
	}
}