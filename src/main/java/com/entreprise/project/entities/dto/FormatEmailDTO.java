package com.entreprise.project.entities.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FormatEmailDTO {
    private String to ;
    private String Subject ;
    private String tomporalPassword;



    public String getBody(String firstName, String lastName, String matricule,
                          String genderUser,
                          String type, String codeVerification) {


       if(type.equals("ForgetPassword")) {
            return "Bonjour "  + firstName +" "+lastName+" . Nous espérons que ce message vous trouve bien.\n" +
                    "Suite à votre tentative de réinitialsation de votre mot de passe avec ce nom d'utilisateur "+matricule+" , nous vous envoyons un nouveau mot de passe pour accèder à l'application.\n " +
                    codeVerification + "\n Cordialement,";
        }else{
            return null;
        }

    }
}

