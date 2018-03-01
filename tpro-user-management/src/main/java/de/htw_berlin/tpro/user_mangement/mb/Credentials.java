package de.htw_berlin.tpro.user_mangement.mb;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class Credentials implements Serializable {
	private static final long serialVersionUID = 1L;
	@Getter @Setter
	private String prename;
	@Getter @Setter
    private String surname;
	@Getter @Setter
    private String email;
	@Getter @Setter
	private String username;
	@Getter @Setter
    private String password;
}
