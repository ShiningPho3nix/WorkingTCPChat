/**
 * TODO Kommentare schreiben
 * 
 * @author Steffen Dworsky, Ramón Schultz
 *
 */

public interface Useradministration {

	/**
	 * Fügt einen Benutzernamen mit dazugehörigen Passwort hinzu.
	 * 
	 * @param username Benutzername (darf kein : enthalten)
	 * @param password Passwort
	 */
	public void addUser(String username, char[] password);

	/**
	 * Prüft ob der übegebene Benutzername zusammen mit dem übergebenen Passwort
	 * existiert.
	 * 
	 * @param username Benutzername (darf kein : enthalten)
	 * @param password Passwort
	 * @return true bei richtiger username-password Kombination
	 */
	public boolean checkUser(String username, char[] password);
}