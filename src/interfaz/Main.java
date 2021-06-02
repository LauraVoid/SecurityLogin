package interfaz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import model.PBKDF2;
import model.User;

public class Main {
	
	private PBKDF2 security;
	

	public Main() {
		security = new PBKDF2();
		
	}

	public void menu() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int value =0;
		while(value<3) {
			System.out.println("Bienvenido seleccione una opción");
			System.out.println("1. Ingresar al menu de usuario \n2. Ingresar al menu de admin \n3. Crear un usuario");
			
			try {
				value= Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			switch(value) {
			
			case 1:
				System.out.println("Ingrese su usuario");
				String username= br.readLine();
				System.out.println("Ingrese su contraseña");
				String passwd= br.readLine();
				boolean result = validateCredentials(username,passwd,false);
				if(result) {
					menuUser();
				}
				
				break;
				
			case 2:
				System.out.println("Ingrese su usuario");
				String usernameAd= br.readLine();
				System.out.println("Ingrese su contraseña");
				String passwdAd= br.readLine();
				boolean resultAd = validateCredentials(usernameAd,passwdAd,true);
				if(resultAd) {
					menuAdmin();
				}			
				
				break;
			case 3:
				System.out.println("Ingrese un usuario");
				String newUser= br.readLine();
				System.out.println("Ingrese una contraseña");
				String newPass= br.readLine();
				//crear user
				
			}
			
			
		}
		System.out.println("Gracias");
	}
	
	public boolean createNewUser(String user, String password) {
		
		boolean result=false;
		
		
		return result;
	}
	
	public boolean validateCredentials(String username, String passwd, boolean isAdmin) throws NoSuchAlgorithmException, InvalidKeySpecException {
		boolean credentials= false;
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;		
	    
	    try {
	    	archivo = new File ("src/interfaz/passwd.txt");
	    	fr = new FileReader (archivo);
	    	br = new BufferedReader(fr);
	    	 String linea;
	    	 //username,password
	    	 
	         while((linea=br.readLine())!=null) {
	        	 String info [] = linea.split(",");
	        	 if(info[0].equals(username)) {
	        		 System.out.println(linea);
	        		 //Validar password
	        		credentials = security.validatePassword(passwd, info[1]);
	        		
	        	 }	            
	         }
	      
		} catch (IOException e) {
			e.printStackTrace();
		}finally{	        
			
	         try{                   
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	      }
	    
		
		return credentials;
	}
	
	public void menuAdmin() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int value = 0;
		while(value<3) {
			System.out.println("Bienvenido admin");
			System.out.println("1. Consultar nombres existentes \n2. Eliminar un usuario \n3. Eliminar una contraseña de usuario");
			value= Integer.parseInt(br.readLine());
			
			switch(value) {
			
			case 1:
				System.out.println("Nombres de los usuarios actuales");
				break;
			case 2:
				System.out.println("Ingrese el nombre de usuario que desea eliminar");
				String username= br.readLine();
				
				break;
			case 3:
				System.out.println("Ingrese el nombre de usuario que desea eliminar la contraseña");
				String usernameD= br.readLine();
				break;
			}
			
			
		}
		
		
	}
	
	public void menuUser() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int value = 0;
		while(value<2) {
			System.out.println("Bienvenido");
			System.out.println("1. Consultar ultimo acceso \n2. Cambiar su contraseña");
			value= Integer.parseInt(br.readLine());
			
			switch(value) {
			
			case 1:
				System.out.println("Su ultimo acceso fue");
				break;
			case 2:
				System.out.println("Ingrese su contraseña nueva");
				String passwd= br.readLine();
				
				break;
			
			}
			
			
		}
		
		
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		
		try {
			main.menu();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
