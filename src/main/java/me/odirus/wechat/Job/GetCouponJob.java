package me.odirus.wechat.Job;

import me.odirus.wechat.Message.IMessageSender;
import me.odirus.wechat.Message.Message;
import me.odirus.wechat.Singleton;
import me.odirus.wechat.Wechat.WechatData;
import me.odirus.wechat.Wechat.WechatUser;
import me.odirus.wechat.coupon.Coupon;
import me.odirus.wechat.coupon.Entrance;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 12:09
 */
public class GetCouponJob implements org.quartz.Job {
	public GetCouponJob() {}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<Message> messageList = getCouponMessage();
		addMessageToSender(messageList);
	}

	private List<Message> getCouponMessage() {
		List<Message> messageList = new ArrayList<Message>();

		Entrance entrance = Singleton.getEntrance();
		List<Coupon> couponList = entrance.getSmzdmCouponList();
		List<Coupon> newCouponList = entrance.saveInDB(couponList);

		for (int i = 0; i < newCouponList.size(); i++) {
			Coupon coupon = newCouponList.get(i);
			String content = "优惠信息:\n" +
				"标题  : " + coupon.getTitle() + "\n" +
				"优惠  : " + coupon.getPromotion() + "\n" +
				"访问链接: " + coupon.getLink() + "\n" +
				"信息来源: " + coupon.getCouponProvider().getDescribe();

			String userName = WechatUser.getSpecifyUser() == null ? "filehelper" : WechatUser.getSpecifyUser().getUserName();

			messageList.add(new Message(userName, content));
		}

		return messageList;
	}

	private void addMessageToSender(List<Message> messageList) {
		IMessageSender messageSender = Singleton.getMessageSender();
		for (int i = 0; i < messageList.size(); i++) {
			messageSender.add(messageList.get(i));
		}
	}
}
