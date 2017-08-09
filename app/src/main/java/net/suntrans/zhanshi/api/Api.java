package net.suntrans.zhanshi.api;


import net.suntrans.zhanshi.bean.AmeterDetailEntity;
import net.suntrans.zhanshi.bean.AmeterDetailEntity3;
import net.suntrans.zhanshi.bean.AmeterEntity;
import net.suntrans.zhanshi.bean.Ammeter3HisEneity;
import net.suntrans.zhanshi.bean.AmmeterHisEneity;
import net.suntrans.zhanshi.bean.DeviceEntity;
import net.suntrans.zhanshi.bean.LoginResult;
import net.suntrans.zhanshi.bean.RoomEntity;
import net.suntrans.zhanshi.bean.SceneEntity;
import net.suntrans.zhanshi.bean.SixEntity;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Looney on 2017/1/4.
 */

public interface Api {

    /**
     * 登录api
     *
     * @param grant_type    默认填password
     * @param client_id     默认填6
     * @param client_secret 默认填test
     * @param username      账号
     * @param password      密码
     * @return
     */
    @FormUrlEncoded
    @POST("oauth/token")
    Observable<LoginResult> login(@Field("grant_type") String grant_type,
                                  @Field("client_id") String client_id,
                                  @Field("client_secret") String client_secret,
                                  @Field("username") String username,
                                  @Field("password") String password);

    @FormUrlEncoded
    @POST("house/scene")
    Observable<SceneEntity> getHomeScene(@Field("id") String id);

    @FormUrlEncoded
    @POST("house/light")
    Observable<DeviceEntity> getRoomLight(@Field("id") String id);

    @FormUrlEncoded
    @POST("house/socket")
    Observable<DeviceEntity> getRoomSocket(@Field("id") String id);

    @FormUrlEncoded
    @POST("house/xenon")
    Observable<DeviceEntity> getRoomXenon(@Field("id") String id);

    @FormUrlEncoded
    @POST("house/environment")
    Observable<SixEntity> getRoomEnv(@Field("id") String id);

    @POST("home/house")
    Observable<RoomEntity> getHomeRoom();

    @POST("energy/ammeter")
    Observable<AmeterEntity> getAmmeter();


    @FormUrlEncoded
    @POST("energy/ammeter/detail")
    Observable<AmeterDetailEntity> getAmmeterDetail(@Field("vtype") String vtype, @Field("sno") String sno);

    @FormUrlEncoded
    @POST("energy/ammeter/detail")
    Observable<AmeterDetailEntity3> getAmmeter3Detail(@Field("vtype") String vtype, @Field("sno") String sno);

    @FormUrlEncoded
    @POST("energy/ammeter/data")
    Observable<AmmeterHisEneity> getAmmeterHistroy(@Field("sno") String sno, @Field("data_type") String data_type);

    @FormUrlEncoded
    @POST("energy/ammeter/data3")
    Observable<Ammeter3HisEneity> getAmmeter3Histroy(@Field("sno") String sno, @Field("data_type") String data_type);

}
