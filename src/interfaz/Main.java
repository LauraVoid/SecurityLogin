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

/**
 * @author juanm
 *
 */
/**
 * @author juanm
 *
 */
public class Main {

	private PBKDF2 security;

	public Main() {
		security = new PBKDF2();

	}

	public void menu() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int value = 0;
		while (value < 4) {
			System.out.println("Bienvenido seleccione una opci�n");
			System.out.println("1. Ingresar al menu de usuario \n2. Ingresar al menu de admin \n3. Crear un usuario");

			try {
				value = Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
				System.err.println("Ingresa un valor v�lido");
			} catch (IOException e) {
				e.printStackTrace();
			}

			switch (value) {

			case 1:
				System.out.println("Ingrese su usuario");
				String username = br.readLine();
				System.out.println("Ingrese su contrase�a");
				String passwd = br.readLine();
				boolean result = validateCredentials(username, passwd, false);
				if (result) {
					menuUser(username);
				} else {
					System.out.println("El usuario o la contrase�a es incorrecta");
				}

				break;

			case 2:
				System.out.println("Ingrese su usuario");
				String usernameAd = br.readLine();
				System.out.println("Ingrese su contrase�a");
				String passwdAd = br.readLine();
				boolean resultAd = validateCredentials(usernameAd, passwdAd, true);
				if (resultAd) {
					menuAdmin();
				} else {
					System.out.println("El usuario o la contrase�a es incorrecta");
				}

				break;
			case 3:
				System.out.println("1. Ingresar un admin \n2. Ingresar un usuario");
				int option = Integer.parseInt(br.readLine());
				if (option == 1) {
					if (!validateAmountAdmin()) {
						System.out.println("Ingrese un usuario");
						String newUser = br.readLine();
						System.out.println("Ingrese una contrase�a");
						String newPass = br.readLine();
						boolean status = createNewUser(newUser, newPass, "true");
						if (status) {
							System.out.println("usuario creado correctamente");

						} else {
							System.out.println("Hubo un error");
						}
					} else {
						System.out.println("Ya existe un admin en el sistema");
					}

				} else if (option == 2) {
					System.out.println("Ingrese un usuario");
					String newUser = br.readLine();
					System.out.println("Ingrese una contrase�a");
					String newPass = br.readLine();
					boolean status = createNewUser(newUser, newPass, "false");
					if (status) {
						// Agregar hora de acceso en accesos.txt
						// User unico
						System.out.println("usuario creado correctamente");

					} else {
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
	 * 
	 * @return false en caso de que NO exista un admin y true en caso de que SI
	 *         exista un admin
	 */
	public boolean validateAmountAdmin() {
		boolean admin = false;

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			archivo = new File("src/interfaz/passwd.txt");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			String linea;
			// username,password

			while ((linea = br.readLine()) != null && !admin) {
				String info[] = linea.split(",");
				if (info[2].equals("true")) {
					admin = true;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return admin;
	}

	/**
	 * Metodo que permite crear un nuevo usuario en la plataforma
	 * 
	 * @param user     nombre de usuario a crear
	 * @param password contrase�a de usuario a crear
	 * @return retorna true si la creaci�n se finaliz� de forma exitosa
	 */

	public boolean createNewUser(String user, String password, String isAdmin) {

		boolean result = false;

		FileWriter fichero = null;
		PrintWriter pw = null;

		try {

			fichero = new FileWriter("src/interfaz/passwd.txt", true);
			pw = new PrintWriter(fichero);
			String securePass = security.generatePassword(password);

			if (!existUser(user)) {
				pw.println(user + "," + securePass + "," + isAdmin);
				result = true;
			} else {
				System.err.println("\n" + "El usuario ingresado ya existe. Intenta con otro nombre de usuario" + "\n");
				result= false;
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

		return result;
	}

	/**
	 * Se encarga de verificar si un username ya existe en la BD
	 * 
	 * @param username
	 * @return false en caso de que no exista. true si existe.
	 */
	public boolean existUser(String username) {
		boolean exist = false;

		try {

			String linea;
			boolean wasFound = false;

			File archivo = null;
			FileReader fichero = null;
			BufferedReader lector = null;

			archivo = new File("src/interfaz/passwd.txt");
			fichero = new FileReader(archivo);
			lector = new BufferedReader(fichero);

			while ((linea = lector.readLine()) != null && !wasFound) {
				String[] user = linea.split(",");
				if (user[0].equalsIgnoreCase(username)) {
					wasFound = true;
					exist = true;
				}
			}
			lector.close();

		} catch (Exception e) {
			System.err.println("\n" + "Ha ocurrido un error. Intenta nuevamente" + "\n");
		}

		return exist;
	}

	/**
	 * Metodo que permite almacenar el ultimo acceso del usuario cuando cierra
	 * sesion
	 * 
	 * @param username el nombre del usuario que desea cerrar sesion
	 */
	public void cerrarSesion(String username) {

		FileWriter fichero = null;
		PrintWriter pw = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {

			fr = new FileReader("src/interfaz/accesos.txt");
			br = new BufferedReader(fr);
			String linea;

			ArrayList<String> data = new ArrayList<String>();
			int conta = 0;
			int i = 0;

			while ((linea = br.readLine()) != null) {

				data.add(linea);

				if (linea.startsWith(username)) {
					i = conta;
				}
				conta++;

			}
			String session = data.get(i);
			LocalDateTime date = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String formattedDate = date.format(myFormatObj);

			session = username + "," + formattedDate;

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
	 * M�todo que permite validar las credenciales del usuario que desea ingresar
	 * 
	 * @param username del usuario que desea ingresar
	 * @param passwd   del usuario que dese ingresar
	 * @param isAdmin
	 * @note el archivo de credenciales se encuentra estructurado as�:
	 *       username,password,isAdmin
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public boolean validateCredentials(String username, String passwd, boolean isAdmin)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		boolean credentials = false;

		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			archivo = new File("src/interfaz/passwd.txt");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			String linea;
			// username,password

			while ((linea = br.readLine()) != null) {
				String info[] = linea.split(",");
				if (info[0].equals(username)) {

					if (isAdmin) {
						if (info[2].equals("true")) {
							// Validar password
							credentials = security.validatePassword(passwd, info[1]);

						}

					} else {
						if (info[2].equals("false")) {
							credentials = security.validatePassword(passwd, info[1]);
						}

					}

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return credentials;
	}
	
	/**
	 * Metodo que permite eliminar un usuario, ingresando como administrador.
	 * 
	 * @param username el nombre del usuario que desea eliminar
	 */
	public void deleteUser(String username) {

		FileWriter fichero = null;
		PrintWriter pw = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {

			fr = new FileReader("src/interfaz/passwd.txt");
			br = new BufferedReader(fr);
			String linea;

			ArrayList<String> data = new ArrayList<String>();

			while ((linea = br.readLine()) != null) {


				if (!linea.startsWith(username)) {
					data.add(linea);
				}

			}
			fichero = new FileWriter("src/interfaz/passwd.txt");
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
	 * Metodo que permite eliminar la contrase�a de un usuario, ingresando como administrador.
	 * 
	 * @param username el nombre del usuario al que le desea eliminar la constrase�a.
	 */
	public void deleteUserPassword(String username) {

		FileWriter fichero = null;
		PrintWriter pw = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {

			fr = new FileReader("src/interfaz/passwd.txt");
			br = new BufferedReader(fr);
			String linea;

			ArrayList<String> data = new ArrayList<String>();

			while ((linea = br.readLine()) != null) {


				if (linea.startsWith(username)) {
					String[] nlinea = linea.split(",");
					nlinea[1] = security.generatePassword("");
					String temp = nlinea[0] + "," + nlinea[1] + "," + nlinea[2];
					linea = temp;
				}
				data.add(linea);

			}
			fichero = new FileWriter("src/interfaz/passwd.txt");
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

	// Juanma
	public void menuAdmin() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int value = 0;
		while (value < 4) {
			System.out.println("Bienvenido admin");
			System.out.println(
					"1. Consultar nombres existentes \n2. Eliminar un usuario \n3. Eliminar una contrase�a de usuario");
			value = Integer.parseInt(br.readLine());

			switch (value) {

			case 1:
				System.out.println("Nombres de los usuarios actuales:\n");
				File archivo = null;
				FileReader fr = null;
				BufferedReader bfr = null;
				try {
					archivo = new File("src/interfaz/passwd.txt");
					fr = new FileReader(archivo);
					bfr = new BufferedReader(fr);
					String linea;
					// username,password

					while ((linea = bfr.readLine()) != null) {
						if (linea.split(",") != null) {
							String info[] = linea.split(",");
							System.out.println(info[0]);
						}
					}
				} catch (Exception e) {
					e.getMessage();
				} finally {

					try {
						if (null != fr) {
							fr.close();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				break;
			case 2:
				System.out.println("Ingrese el nombre de usuario que desea eliminar");
				// TODO
				String username = br.readLine();
				if (existUser(username)) {
					deleteUser(username);
					System.out.println(username + " eliminado correctamente.");
				} else {
					System.out.println("El usuario *" + username + "* no existe!");
				}
				break;
			case 3:
				System.out.println("Ingrese el nombre de usuario que desea eliminar la contrase�a");
				// TODO
				String usernameD = br.readLine();
				if(existUser(usernameD)) {
					deleteUserPassword(usernameD);
					System.out.println("Se ha eliminado la password de " + usernameD + " correctamente.");
				} else {
					System.out.println("El usuario *" + usernameD + "* no existe!");
				}
				break;
			}

		}

	}

	/**
	 * M�todo que muestra el men� del usuario, y realiza todas sus funciones tanto
	 * la de consultar su �ltimo acceso en la BD y cambiar su contrase�a
	 * 
	 * @param username
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void menuUser(String username) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int value = 0;
		while (value < 4) {
			System.out.println("Bienvenido");
			System.out.println("1. Consultar ultimo acceso \n2. Cambiar su contrase�a \n3. Cerrar sesion");

			try {
				value = Integer.parseInt(br.readLine());
			} catch (Exception e) {
				System.err.println("Ingresa un valor v�lido");
			}

			switch (value) {

			case 1:
				try {

					String linea;
					boolean wasFound = false;

					File archivo = null;
					FileReader fichero = null;
					BufferedReader lector = null;

					archivo = new File("src/interfaz/accesos.txt");
					fichero = new FileReader(archivo);
					lector = new BufferedReader(fichero);

					while ((linea = lector.readLine()) != null && !wasFound) {
						String[] user = linea.split(",");
						if (user[0].equalsIgnoreCase(username)) {
							String[] date = user[1].split(" ");
							System.out.println(
									"\n" + "Te conectaste el d�a: " + date[0] + "\n" + "A las: " + date[1] + "\n");
							wasFound = true;
						}
					}
					lector.close();

				} catch (Exception e) {
					System.err.println("\n" + "No ha sido posible. Intenta nuevamente" + "\n");
				}
				break;
			case 2:
				System.out.println("Ingrese su contrase�a nueva");
				String passwd = br.readLine();

				try {

					String linea;
					String ruta = "src/interfaz/passwdcopia.txt";
					File nuevoArchivo = new File(ruta);

					// Leer archivo existente
					File archivo = null;
					FileReader fichero = null;
					BufferedReader lector = null;

					// Escribir archivo actualizado
					FileWriter ficheroWrite = null;
					PrintWriter escritor = null;

					archivo = new File("src/interfaz/passwd.txt");

					if (archivo.exists() && passwd != null) {

						fichero = new FileReader(archivo);
						lector = new BufferedReader(fichero);

						if (nuevoArchivo.exists()) {
							System.err.println("\n" + "Ha ocurrido un problema. Intenta nuevamente" + "\n");
						} else {
							nuevoArchivo.createNewFile();
							ficheroWrite = new FileWriter(nuevoArchivo);
							escritor = new PrintWriter(ficheroWrite);
							while ((linea = lector.readLine()) != null) {

								String[] user = linea.split(",");
								if (user[0].equalsIgnoreCase(username)) {
									escritor.println(user[0] + "," + security.generatePassword(passwd) + "," + "false");
								} else {
									escritor.println(linea);
								}
							}

							escritor.close();
							lector.close();

							// Renombrar archivo.
							archivo.delete();
							nuevoArchivo.renameTo(archivo);
							System.out.println("\n" + "Contrase�a cambiada con �xito" + "\n");
						}
					} else {
						System.err.println("\n" + "Ha ocurrido un error. Intenta nuevamente" + "\n");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:
				cerrarSesion(username);
				System.out.println("Su sesion ha finalizado");
				value = 4;
				break;
			default:
				System.err.println("Ingresa un n�mero v�lido. Intenta nuevamente");
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
