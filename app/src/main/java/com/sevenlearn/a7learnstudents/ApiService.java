package com.sevenlearn.a7learnstudents;

import android.content.Context;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL="http://expertdevelopers.ir/api/v1/";
    private RetrofitApiService apiService;
    public ApiService(Context context,String requestTag) {
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request oldRequest=chain.request();
                        Request.Builder newRequestBuilder=oldRequest.newBuilder();
                        newRequestBuilder.addHeader("Acccept","application/json");
//                        newRequestBuilder.addHeader("Authorization","YOUR TOKEN");
                        return chain.proceed(newRequestBuilder.build());
                    }
                }).build();

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        apiService=retrofit.create(RetrofitApiService.class);
    }

    public void saveStudent(String firstName, String lastName, String course, int score, final SaveStudentCallback callback) {
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("first_name",firstName);
        jsonObject.addProperty("last_name",lastName);
        jsonObject.addProperty("course",course);
        jsonObject.addProperty("score",score);
        apiService.saveStudent(jsonObject).enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });

    }

    public void getStudents(final StudentListCallback callback) {
        apiService.getStudents()
                .enqueue(new Callback<List<Student>>() {
                    @Override
                    public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                        callback.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Student>> call, Throwable t) {
                        callback.onError(new Exception(t));
                    }
                });
    }

    public void cancel(){

    }

    public interface SaveStudentCallback {
        void onSuccess(Student student);

        void onError(Exception error);
    }

    public interface StudentListCallback{
        void onSuccess(List<Student> students);
        void onError(Exception error);
    }
}
