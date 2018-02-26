/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package entity;

/**
 * <pre>
 * 基金实体。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * @date 2017年9月1日 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class FoundationEntity {

	private String date;
	
	private double dwjz;
	
	private double jjzzl;

	/**
	 * @return 返回 date。
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date 设置 date。
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return 返回 dwjz。
	 */
	public double getDwjz() {
		return dwjz;
	}

	/**
	 * @param dwjz 设置 dwjz。
	 */
	public void setDwjz(double dwjz) {
		this.dwjz = dwjz;
	}

	/**
	 * @return 返回 jjzzl。
	 */
	public double getJjzzl() {
		return jjzzl;
	}

	/**
	 * @param jjzzl 设置 jjzzl。
	 */
	public void setJjzzl(double jjzzl) {
		this.jjzzl = jjzzl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FoundationEntity [date=" + date + ", dwjz=" + dwjz + ", jjzzl=" + jjzzl + "]";
	}
	
	
	
	
	

}
