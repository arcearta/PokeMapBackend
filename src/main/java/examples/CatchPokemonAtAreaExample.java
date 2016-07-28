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


import POGOProtos.Map.Fort.FortDataOuterClass;
import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.MapObjects;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EncounterResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.Log;
import okhttp3.OkHttpClient;

import java.util.List;

public class CatchPokemonAtAreaExample {

	/**
	 * Catches a pokemon at an area.
	 */
	public static void main(String[] args) {
		OkHttpClient http = new OkHttpClient();
		RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo auth = null;
		try {
			//auth = new PtcLogin(http).login(com.pokegoapi.examples.ExampleLoginDetails.LOGIN, com.pokegoapi.examples.ExampleLoginDetails.PASSWORD);
			// or google
			String token = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjBiZDEwY2JmMDM2OGQ2MWE0NDBiZjYxZjNiM2EyZDI0NGExODQ5NDcifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdF9oYXNoIjoib2ljcGdidS00Q1d1SFdLSEdNRDZ4dyIsImF1ZCI6Ijg0ODIzMjUxMTI0MC03M3JpM3Q3cGx2azk2cGo0Zjg1dWo4b3RkYXQyYWxlbS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExMTQyMTY1MjcxMjA1NzEwMDc1MCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI4NDgyMzI1MTEyNDAtNzNyaTN0N3Bsdms5NnBqNGY4NXVqOG90ZGF0MmFsZW0uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJlbWFpbCI6ImFhcmlhc3RhQGdtYWlsLmNvbSIsImlhdCI6MTQ2OTU0Nzk5NiwiZXhwIjoxNDY5NTUxNTk2fQ.CEFnZW6nikCiGiF-_YtvgiZuK7GRHDlUlGCor0ZkCYKYb2ULntMj741JMWaWnG_RScpj_lycsFrAmGlxvy9qdv-0oOM5bmOIGjYPQVBSrYXncJ5lazAHlnIplUICHgv_bfE00C_yuaShCkLgBpXoaOgHdQp86WlBqLHb8CN3NBJk2CUUKZa6skTFGDOEgTgwSE1JEaanTTKr-3b6sfod-hwTbEIsMO5IoNNma4jp7E1LACl_3VBN1hOA4ZbTvOReSSVztkcIIdPTcM8styinPAg983u5nn_fApxHcvgK-m5-SUS9KWp9EsJkVQAstbP79Dg5SJrnq3ubm0r-4Z5z9g";

			RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo.Builder builder = RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo.newBuilder();
			builder.setProvider("google");
			builder.setToken(RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo.JWT.newBuilder().setContents(token).setUnknown2(59).build());
			auth = builder.build();

			//auth = new GoogleLogin(http).login("", ""); // currently uses oauth flow so no user or pass needed
			PokemonGo go = new PokemonGo(auth, http);
			// set location
			go.setLocation(6.254010, -75.578931, 0);

			List<CatchablePokemon> catchablePokemon = go.getMap().getCatchablePokemon();
			System.out.println("Pokemon in area:" + catchablePokemon.size());

			for (CatchablePokemon cp : catchablePokemon) {
				// You need to Encounter first.
				EncounterResult encResult = cp.encounterPokemon();
				// if encounter was succesful, catch
				if (encResult.wasSuccessful()) {
					System.out.println("Encounted:" + cp.getPokemonId());
					CatchResult result = cp.catchPokemonWithRazzBerry();
					System.out.println("Attempt to catch:" + cp.getPokemonId() + " " + result.getStatus());
				}

			}


			MapObjects spawnPoints = go.getMap().getMapObjects(6.254010, -75.578931);
			System.out.println("Point in area:" + spawnPoints.isComplete());



			for (FortDataOuterClass.FortData cp : spawnPoints.getGyms()) {
				// You need to Encounter first.

				// if encounter was succesful, catch

					System.out.println("latitud:" + cp.getLatitude());
				System.out.println("longitud:" + cp.getLongitude());
				System.out.println("nombre:" + cp.getSponsor().name());
				System.out.println("color:" + cp.getOwnedByTeam().name());


			}


		} catch (LoginFailedException | RemoteServerException e) {
			// failed to login, invalid credentials, auth issue or server issue.
			Log.e("Main", "Failed to login or server issue: ", e);

		}
	}
}
