package com.inin;

import com.mypurecloud.sdk.Configuration;
import com.mypurecloud.sdk.api.PresenceApi;
import com.mypurecloud.sdk.api.UsersApi;
import com.mypurecloud.sdk.model.OrganizationPresence;
import com.mypurecloud.sdk.model.OrganizationPresenceEntityListing;
import com.mypurecloud.sdk.model.UserMe;
import com.mypurecloud.sdk.model.UserPresence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            // Configure SDK settings
            Configuration.getDefaultApiClient().setAccessToken("accesstoken");
            Configuration.getDefaultApiClient().setBasePath("https://api.mypurecloud.com");

            // Instantiate APIs
            UsersApi usersApi = new UsersApi();
            PresenceApi presenceApi = new PresenceApi();

            // Validate token with GET /api/v2/users/me (throws an exception if unauthorized)
            UserMe me = usersApi.getMe(Arrays.asList("presence"));
            System.out.println("Hello " + me.getName());

            // Get presences
            OrganizationPresenceEntityListing presences = presenceApi.getPresencedefinitions(1, 25, "false");

            // Find Available and Break org presences
            OrganizationPresence availablePresence = null;
            OrganizationPresence breakPresence = null;
            for (int i = 0; i < presences.getEntities().size(); i++) {
                OrganizationPresence presence = presences.getEntities().get(i);

                // Ignore non-primary (i.e. user-defined presences) for this tutorial
                if (!presence.getPrimary()) continue;

                // Check system presences
                if (presence.getSystemPresence().equalsIgnoreCase("available")) {
                    availablePresence = presence;
                } else if (presence.getSystemPresence().equalsIgnoreCase("break")) {
                    breakPresence = presence;
                }
            }

            // Verify results
            if (availablePresence == null || breakPresence == null) {
                throw new Exception("Failed to find presences!");
            }

            //Notifications not implemented in the Java tutorial

            // Wait for user input
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Press enter to set status to available");
            br.readLine();

            // Set presence to Available
            UserPresence body = new UserPresence();
            body.setPresenceDefinition(availablePresence);
            UserPresence presenceResponse = presenceApi.patchUserIdPresencesSourceId(me.getId(), "PURECLOUD", body);

            // Wait for user input
            System.out.print("Press enter to set status to break");
            br.readLine();

            // Set presence to Available
            body = new UserPresence();
            body.setPresenceDefinition(breakPresence);
            presenceResponse = presenceApi.patchUserIdPresencesSourceId(me.getId(), "PURECLOUD", body);

            System.out.print("Application complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
