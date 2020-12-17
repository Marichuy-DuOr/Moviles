package pansitosapp.mx.http;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Client {

    @POST("login")
    Call<JsonObject> onLogin(@Body JsonObject data);

    @POST("register")
    Call<JsonObject> onRegister(@Body JsonObject data);

    @GET("elUsuario")
    Call<JsonObject> getelUsuario(@Header("user_token") String token);

    @GET("productos")
    Call<JsonObject> getAllProductos(@Header("user_token") String token);

    @GET("producto/{id}")
    Call<JsonObject> getProducto(@Header("user_token") String token, @Path("id") Integer id);

    @DELETE("producto/{id}")
    Call<JsonObject> deleteProducto(@Header("user_token") String token, @Path("id") Integer id);

    @POST("producto")
    Call<JsonObject> createProducto(@Header("user_token") String token, @Body JsonObject data);

    @PUT("producto")
    Call<JsonObject> modifyProducto(@Header("user_token") String token, @Body JsonObject data);

    @GET("pedidosActivos/{estado}")
    Call<JsonObject> getAllPedidosActivos(@Header("user_token") String token, @Path("estado") Integer estado);

    @GET("detalles/{id}")
    Call<JsonObject> getDetalles(@Header("user_token") String token, @Path("id") Integer id);

    @PUT("completarPedido")
    Call<JsonObject> completarPedido(@Header("user_token") String token, @Body JsonObject data);

}
