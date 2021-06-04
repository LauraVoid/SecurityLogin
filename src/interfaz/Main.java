package interfaz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import model.PBKDF2;
import model.User;

public class Main {
	
	private PBKDF2 security;
	

	public Main() {
		security = new PBKDF2();
		
	}

	public void menu() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int value =0;
		while(value<4) {
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
					menuUser(username);
				}else {
					System.out.println("El usuario o la contraseña es incorrecta");
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
				}else {
					System.out.println("El usuario o la contraseña es incorrecta");
				}		
				
				break;
			case 3:
				System.out.println("1. Ingresar un admin \n2. Ingresar un usuario");
				int option = Integer.parseInt(br.readLine());
				if(option == 1) {
					if(!validateAmountAdmin()) {
						System.out.println("Ingrese un usuario");
						String newUser= br.readLine();
						System.out.println("Ingrese una contraseña");
						String newPass= br.readLine();
						boolean status =createNewUser(newUser,newPass,"true");
						if(status) {
							System.out.println("usuario creado correctamente");
							
						}else {
							System.out.println("Hubo un error");
						}
					}else {
						System.out.println("Ya existe un admin en el sistema");
					}
					
				}else if(option == 2) {
					System.out.println("Ingrese un usuario");
					String newUser= br.readLine();
					System.out.println("Ingrese una contraseña");
					String newPass= br.readLine();
					boolean status =createNewUser(newUser,newPass,"false");
					if(status) {
						System.out.println("usuario creado correctamente");
						
					}else {
						System.out.println("Hubo un error");
					}
					
				}				
				
				break;
				
				
			}
			
			
		}
		System.out.println("Gracias");
	}
	/**
	 * Metodo encargado de verificar un unico usuario admin dentro del sistema
	 * @return false en caso de que NO exista un admin y true en caso de que SI exista un admin
	 */
	public boolean validateAmountAdmin() {
		boolean admin=false;
		

		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;		
	    
	    try {
	    	archivo = new File ("src/interfaz/passwd.txt");
	    	fr = new FileReader (archivo);
	    	br = new BufferedReader(fr);
	    	 String linea;
	    	 //username,password
	    	 
	         while((linea=br.readLine())!=null && !admin) {
	        	 String info [] = linea.split(",");
	        	 if(info[2].equals("true")) {
	        		 admin = true;
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
		
		
		return admin;
	}
	/**
	 * Metodo que permite crear un nuevo usuario en la plataforma
	 * @param user nombre de usuario a crear
	 * @param password contraseña de usuario a crear
	 * @return retorna true si la creación se finalizó de forma exitosa
	 */
	
	public boolean createNewUser(String user, String password, String isAdmin) {
		
		boolean result=false;
		
		 FileWriter fichero = null;
	        PrintWriter pw = null;
	        
	        try
	        {
	        	
	            fichero = new FileWriter("src/interfaz/passwd.txt",true);
	            pw = new PrintWriter(fichero);
	            String securePass = security.generatePassword(password);	      
	           
	           pw.println(user +","+securePass+","+isAdmin);          
	                      
	            

	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	           try {	           
	           if (null != fichero)
	              fichero.close();
	             
	           		result = true;
	           } catch (Exception e2) {
	              e2.printStackTrace();
	           }
	        }		
		
		
		return result;
	}
	/**
	 * Metodo que permite almacenar el ultimo acceso del usuario cuando cierra sesion
	 * @param username el nombre del usuario que desea cerrar sesion
	 */
	public void cerrarSesion(String username) {
		
		FileWriter fichero = null;
        PrintWriter pw = null;
        FileReader fr = null;
	    BufferedReader br = null;
        
        try
        {           
            
            fr = new FileReader ("src/interfaz/accesos.txt");
	    	br = new BufferedReader(fr);
	    	String linea;
	    	
	    	ArrayList<String> data = new ArrayList<String>();
	    	int conta =0;
	    	int i=0;
	    	
	    	
	    	while((linea = br.readLine()) != null) {
	    		
	    		data.add(linea);
	    		
	    		if(linea.startsWith(username)) {
	    			i= conta;	    			
	    		}
	    		conta++;
	    		
	    	}
	    	String session = data.get(i);
	    	LocalDateTime date = LocalDateTime.now(); 
	    	DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	    	String formattedDate = date.format(myFormatObj);
	    	
	    	session = username+","+formattedDate;
	    	
	    	data.set(i, session);
	    	fichero = new FileWriter("src/interfaz/accesos.txt");
            pw = new PrintWriter(fichero);
	    	
	    	for (int j = 0; j < data.size(); j++) {
				pw.println(data.get(j));
			} 

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {	           
           if (null != fichero)
              fichero.close();
           
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }		
		
		
	}
	/**
	 *  Método que permite validar las credenciales del usuario que desea ingresar
	 * @param username del usuario que desea ingresar
	 * @param passwd del usuario que dese ingresar
	 * @param isAdmin
	 * @note el archivo de credenciales se encuentra estructurado así: username,password,isAdmin
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
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
	        		 
	        		 if(isAdmin) {
	        			 if(info[2].equals("true")) {		        			 
			        		 //Validar password
			        		credentials = security.validatePassword(passwd, info[1]);
			        		
		        			 
		        		 }
	        			 
	        		 }else {
	        			 if(info[2].equals("false")) {
	        				 credentials = security.validatePassword(passwd, info[1]);	 
	        			 }
	        			 
	        			 
	        		 }
	        		
	        		
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
				//TODO
				break;
			case 2:
				System.out.println("Ingrese el nombre de usuario que desea eliminar");
				//TODO
				String username= br.readLine();
				
				break;
			case 3:
				System.out.println("Ingrese el nombre de usuario que desea eliminar la contraseña");
				//TODO
				String usernameD= br.readLine();
				break;
			}
			
			
		}
		
		
	}
	
	public void menuUser(String username) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int value = 0;
		while(value<4) {
			System.out.println("Bienvenido");
			System.out.println("1. Consultar ultimo acceso \n2. Cambiar su contraseña \n3. Cerrar sesion");
			value= Integer.parseInt(br.readLine());
			
			switch(value) {
			
			case 1:
				System.out.println("Su ultimo acceso fue");
				//TODO
				break;
			case 2:
				System.out.println("Ingrese su contraseña nueva");
				//TODO
				String passwd= br.readLine();				
				break;
			case 3:
				cerrarSesion(username);
				System.out.println("Su sesion ha finalizado");
				value = 4;
				break;
			
			}
			
			
		}
		
		
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Main main = new Main();
		
		try {
			main.menu();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
