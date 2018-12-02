import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * TODO Kommentare schreiben
 * 
 * @author Steffen Dworsky, Ramón Schultz
 *
 */
public class Useradmin implements Useradministration {

	private String filename = "passwords.txt";
    // Länge des Salts in Byte
    private int saltlength = 32; 
    

	/**
     * Fügt einen Benutzernamen mit dazugehörigen Passwort hinzu.
     * Dabei wird ein zufälliger Salt erstellt, mit dem das Passwort gehasht wird.
     * Folgendes Format wird daraufhin in eine Datei geschrieben: username:salt:hash
	 * @param username Benutzername (darf kein : enthalten)
	 * @param password Passwort
	 */
	@Override
	public void addUser(String username, char[] password)
	{
		byte[] salt = new byte[saltlength];
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);
		String hash = createHash(password,salt);
		addToFile(username + ":" + toHex(salt) + ":" + hash);
	}

	/* 
     * Prüft ob der übegebene Benutzername zusammen mit dem übergebenen Passwort existiert.
     * Hasht das übergebene Passwort und vergleicht dieses mit dem gespeicherten Hash.
	 * @param username Benutzername (darf kein : enthalten)
	 * @param password Passwort
	 * @return true bei richtiger username-password Kombination
	 */
	@Override
	public boolean checkUser(String username, char[] password)
	{
		String saltHash = getSaltHash(username);
		if (saltHash == null)
		{
			return false;
		} else{
			String saltHex = saltHash.split(":")[0];
			String hash = saltHash.split(":")[1];
			byte[] salt = fromHex(saltHex);
			String generatedHash = createHash(password, salt);
			if (generatedHash.equals(hash))
			{
				return true;
			}
			else{
				return false;
			}
		}

	}

	/**
     * Hängt eine Zeichenkette hinten an eine Datei an.
	 * @param input die anzuhängende Zeichenkette
	 */
	private void addToFile(String input){
        PrintWriter writer = null;
		try {
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new PrintWriter(new FileWriter(file,true));
			writer.println(input);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (writer != null)
            {
                writer.close();
            }
        }
	}

	/**
     * Lädt den Hash und den Salt eines Benutzers aus der Datei 'filename'
	 * @param username Benutzername
	 * @return Salt und Hash in der Form salt:hash
	 */
	private String getSaltHash(String username){
		String saltHash = null;
		Scanner scanner = null;
		try {
			scanner = new Scanner(new BufferedReader(new FileReader(filename)));
			String line = null;
			while((line = scanner.nextLine()) != null){
				String[] part = line.split(":");
				if (part[0].equals(username))
				{
					saltHash = part[1] + ":" + part[2];
				}

			}
		}catch (Exception e){

		}finally {
			if (scanner != null) { 
				scanner.close(); 
			}
		}

		return saltHash;

	}

	/**
     * Erstellt einen Hash aus dem übergebenen Passwort und Salt
	 * @param password Passwort
	 * @param salt Salt
	 * @return Hash
	 */
	private String createHash(char[] password, byte[] salt)
	{
		byte[] hash = null;
        // Erstellt ein Key Objekt mit 1000 Hash-Iterationen
		PBEKeySpec spec = new PBEKeySpec(password, salt, 1000, saltlength * 8);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            // Passwort aus dem Speicher löschen nicht möglich!!
            SecretKey key = skf.generateSecret(spec);
            hash = key.getEncoded();
        } 
        catch (Exception e)
        {
        }finally{
            spec.clearPassword();
        }

        return toHex(hash);

    }

	/**
     * Erstellt einen Hexadezimal String aus einem Byte Array
	 * @param byteArray 
	 * @return Hexadezimal String
	 */
	private String toHex(byte[] byteArray) {
		StringBuilder builder = new StringBuilder();
		for(byte b : byteArray) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	/**
     * Erstellt ein Byte Array aus einem Hexadezimal String
	 * @param hex
	 * @return Byte Array
	 */
	private static byte[] fromHex(String hex)
	{
		byte[] binary = new byte[hex.length() / 2];
		for(int i = 0; i < binary.length; i++)
		{
			binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
		}
		return binary;
	}



	/**
     * Gibt in der Konsole aus wie das Programm zu benutzen ist.
	 */
	private static void usage()
	{
		System.out.println("Bitte korrekte Kommandozeilen-Argumente übergeben:");
		System.out.println("addUser <BENUTZERNAME>");
		System.out.println("checkUser <BENUTZERNAME> ");
	}

	/**
     * mittels addUser wird ein Benutzer hinzugefügt falls dieser noch nicht existiert
     * checkUser Prüft ob der Benutzername mit eingegebenem Passwort existiert.
	 * @param args Zu übergebene Parameter: addUser/checkUser Beutzername
	 */
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			usage();
			System.exit(0);
		}
		String operation = args[0];
		String username = args[1];
		Useradmin Admin = new Useradmin();
        char[] password = null;

		switch (operation)
		{
		case "addUser":
            // Prüfen ob Benutzer existiert
			if(Admin.getSaltHash(username) == null)
			{
                password = System.console().readPassword("Password: ");
				Admin.addUser(username,password);
                System.out.println("User erfolgreich hinzugefügt");
			} else {
				System.out.println("User existiert bereits");
			}
			break;
		case "checkUser":
            password = System.console().readPassword("Password: ");
            if (Admin.checkUser(username,password))
			{
				System.out.println("User erfolgreich validiert");
			}
			else{
				System.out.println("Falsche User-Passwort Eingabe");
			}
			break;
		default:
			usage();
		}
        if (password != null){
            java.util.Arrays.fill(password, ' ');
        }
        
	}
}