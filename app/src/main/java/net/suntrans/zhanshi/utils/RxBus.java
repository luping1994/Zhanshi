package net.suntrans.zhanshi.utils;


import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * RxBus第二种实现方法
 * <p/>
 * Subject同时充当了Observer和Observable的角色，Subject是非线程安全的，
 * 要避免该问题，需要将 Subject转换为一个 SerializedSubject ，
 * 上述RxBus类中把线程非安全的PublishSubject包装成线程安全的Subject。

 * PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
 * <p/>
 * ofType操作符只发射指定类型的数据，其内部就是filter+cast
 */
public class RxBus
{

    private static volatile RxBus mInstance;

    private final Subject bus;


    public RxBus()
    {

        bus = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 单例模式RxBus2
     *
     * @return
     */
    public static RxBus getInstance()
    {

        RxBus rxBus = mInstance;
        if (mInstance == null)
        {
            synchronized (RxBus.class)
            {
                rxBus = mInstance;
                if (mInstance == null)
                {
                    rxBus = new RxBus();
                    mInstance = rxBus;
                }
            }
        }

        return rxBus;
    }


    /**
     * 发送消息
     *
     * @param object
     */
    public void post(Object object)
    {

        bus.onNext(object);
    }

    /**
     * 接收消息
     *
     * @param eventType
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObserverable(Class<T> eventType)
    {

        return bus.ofType(eventType);
    }
}
