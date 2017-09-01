/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package entity;

/**
 * <pre>
 * 车次实体。
 * </pre>
 * @author 王文辉  wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * @date 2017年9月1日 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class Train {
	/**
	 * 车次编号
	 */
	private String trainNumber;
	/**
	 * 出发站
	 */
	private String trainStartStation;
	/**
	 * 终点站
	 */
	private String trainEndStation;
	/**
	 * 出发时间
	 */
	private String startTime;
	/**
	 * 到达时间
	 */
	private String arriveTime;
	/**
	 * 是否存在二等座
	 */
	private String secondSeatCout;
	/**
	 * @return 返回 trainNumber。
	 */
	public String getTrainNumber() {
		return trainNumber;
	}
	/**
	 * @param trainNumber 设置 trainNumber。
	 */
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	/**
	 * @return 返回 trainStartStation。
	 */
	public String getTrainStartStation() {
		return trainStartStation;
	}
	/**
	 * @param trainStartStation 设置 trainStartStation。
	 */
	public void setTrainStartStation(String trainStartStation) {
		this.trainStartStation = trainStartStation;
	}
	/**
	 * @return 返回 trainEndStation。
	 */
	public String getTrainEndStation() {
		return trainEndStation;
	}
	/**
	 * @param trainEndStation 设置 trainEndStation。
	 */
	public void setTrainEndStation(String trainEndStation) {
		this.trainEndStation = trainEndStation;
	}
	/**
	 * @return 返回 startTime。
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime 设置 startTime。
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return 返回 arriveTime。
	 */
	public String getArriveTime() {
		return arriveTime;
	}
	/**
	 * @param arriveTime 设置 arriveTime。
	 */
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	/**
	 * @return 返回 secondSeatCout。
	 */
	public String getSecondSeatCout() {
		return secondSeatCout;
	}
	/**
	 * @param secondSeatCout 设置 secondSeatCout。
	 */
	public void setSecondSeatCout(String secondSeatCout) {
		this.secondSeatCout = secondSeatCout;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Train [trainNumber=" + trainNumber + ", trainStartStation=" + trainStartStation + ", trainEndStation="
				+ trainEndStation + ", startTime=" + startTime + ", arriveTime=" + arriveTime + ", secondSeatCout="
				+ secondSeatCout + "]";
	}
	/**
	 * @param trainNumber
	 * @param trainStartStation
	 * @param trainEndStation
	 * @param startTime
	 * @param arriveTime
	 * @param secondSeatCout
	 */
	public Train(String trainNumber, String trainStartStation, String trainEndStation, String startTime,
			String arriveTime, String secondSeatCout) {
		this.trainNumber = trainNumber;
		this.trainStartStation = trainStartStation;
		this.trainEndStation = trainEndStation;
		this.startTime = startTime;
		this.arriveTime = arriveTime;
		this.secondSeatCout = secondSeatCout;
	}
	
	

}
