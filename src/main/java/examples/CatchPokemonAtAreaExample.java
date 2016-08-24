/*
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package examples;

import POGOProtos.Map.Pokemon.MapPokemonOuterClass;

import POGOProtos.Map.Pokemon.NearbyPokemonOuterClass;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.pokegoapi.api.PokemonGo;

import com.pokegoapi.api.map.MapObjects;

import com.pokegoapi.auth.*;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.Log;
import okhttp3.OkHttpClient;

import java.io.*;

import java.util.Scanner;


public class CatchPokemonAtAreaExample {

    public static GoogleCredentialProvider auth;

    public InputStream getUrl() {
        return this.getClass().getResourceAsStream("/" + "client_secret.json");
    }


    /**
     * Catches a pokemon at an area.
     */
    public static void main(String[] args) {
        //aut_code=4/_rd0zUigh7awDOjpOxm165EgbhicJVOXLC3UXclp4V8
        //refresh=1/tonF2rg3bavTh84gxnN9OC3_xLVr5YK5ZO1xWwNeGmE
        CatchPokemonAtAreaExample catchPokemonAtAreaExample = new CatchPokemonAtAreaExample();

        OkHttpClient http = new OkHttpClient();
        try {
            /*RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo.Builder builder = RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo.newBuilder();
            builder.setProvider("google");
            builder.setToken(RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo.JWT.newBuilder().setContents(token.getIdToken()).setUnknown2(59).build());
            builder.build();*/

          // GoogleAutoCredentialProvider auth = new GoogleAutoCredentialProvider(http, "iotpruebas@gmail.com", "Sura2016");

           String auth_code = "4/zpZA538lVhOw-3ei_EFgwJKHR4C0UOOHt2eX79tU8sk";

            String refreshToken = "";

            // Exchange auth code for access token
            //refreshToken = getRefreshToken(catchPokemonAtAreaExample, auth_code);
            //System.out.println("Refresh token Generado: " + refreshToken);

            refreshToken = "1/tonF2rg3bavTh84gxnN9OC3_xLVr5YK5ZO1xWwNeGmE";

           // SimpleCredentialProvider auth = new SimpleCredentialProvider(http, refreshToken);

           // GoogleUserCredentialProvider provider = autWitGoogle(http);

            //System.out.println(provider.getRefreshToken());
            GoogleCredentialProvider auth = new GoogleCredentialProvider(http, refreshToken);

            //GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
            //provider.login(auth_code);
            //GoogleCredentialProvider auth = new GoogleCredentialProvider(http, provider.getRefreshToken());

            //PokemonGo go = new PokemonGo(new PtcCredentialProvider(http, "pokeservices", "pokeservices"), http);
            System.out.println("1111111");
            PokemonGo go = new PokemonGo(auth, http);

            System.out.println("222222");
            try {
                Thread.sleep(6000);
            }catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Username:" + go.getPlayerProfile().getPlayerData().getUsername());

            //go.setLocation(6.254010, -75.578931, 1);
            go.setLocation(6.2538345, -75.57843804, 1);

            MapObjects spawnPoints = null;

            try {
                spawnPoints = go.getMap().getMapObjects(9);

            }catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Pokemon in area:" + spawnPoints.getCatchablePokemons().size());
            for (MapPokemonOuterClass.MapPokemon cp : spawnPoints.getCatchablePokemons()) {
                System.out.println("Pokemon name:" + cp.getPokemonId().name());
                System.out.println("Pokemon id:" + cp.getPokemonId().getNumber());
                System.out.println("Pokemon latitud:" + cp.getLatitude());
                System.out.println("Pokemon longitud:" + cp.getLongitude());
            }

            System.out.println("-------------------------------------------");
            System.out.println("Gym in area:" + spawnPoints.getGyms().size());
            System.out.println("Stop in area:" + spawnPoints.getPokestops().size());

            System.out.println("Near in area:" + spawnPoints.getNearbyPokemons().size());

            for(NearbyPokemonOuterClass.NearbyPokemon pokemon : spawnPoints.getNearbyPokemons()){
                System.out.println(pokemon.getPokemonId());
            }


        } catch (LoginFailedException e) {
            Log.e("Main", "Failed to login or server issue: ", e);
            e.printStackTrace();
        } catch (RemoteServerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static GoogleUserCredentialProvider autWitGoogle(OkHttpClient http) throws LoginFailedException, RemoteServerException {
        GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);

        // in this url, you will get a code for the google account that is logged
        System.out.println("Please go to " + GoogleUserCredentialProvider.LOGIN_URL);
        System.out.println("Enter authorisation code:");


        // Ask the user to enter it in the standart input
        Scanner sc = new Scanner(System.in);
        String access = sc.nextLine();

        // we should be able to login with this token
        provider.login(access);
        return provider;
    }


    private static String getRefreshToken(CatchPokemonAtAreaExample catchPokemonAtAreaExample, String auth_code) throws IOException {

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(), new BufferedReader(new InputStreamReader(catchPokemonAtAreaExample.getUrl()
                        )));


        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://www.googleapis.com/oauth2/v4/token",
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret(),
                        auth_code,
                        "")  // Specify the same redirect URI that you use with your web
                        // app. If you don't have a web version of your app, you can
                        // specify an empty string.
                        .execute();


        String accessToken = tokenResponse.getAccessToken();
        String refreshToken =  tokenResponse.getRefreshToken();//"1/XQ6Nd4Xk_2mncX6Ajy54qyMezHBbfu-RY6qtUbuWU-Q";

        System.out.println(refreshToken);
        return refreshToken;
    }
}
