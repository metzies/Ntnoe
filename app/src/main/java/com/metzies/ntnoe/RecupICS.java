/*
 * Ce fichier fait partie du projet NTNOE sur Android.
 * 
 * Les développeurs mettent ce projet à disposition de tous sous les conditions suivantes:
 * 
 * Vous avez le droit d'examiner et tester le code.
 * 
 * Vous avez le droit de copier et modifier le code à condition de citer clairement et publiquement vos sources.
 * 
 * Vous n'avez pas le droit de désigner ce code (modifié ou non) comme entièrement le votre.
 * 
 * Vous n'avez pas le droit de commercialiser ce code sans le consentement écrit des développeurs originaux.
 * 
 * Les développeurs gardent le droit de regard sur toute oeuvre dérivée de ce code là.
 * 
 * Les développeurs originaux fournissent ce code tel quel, sans garantie.
 * 
 * Pour plus d'information sur les termes de la licence, contacter pierre-yves.bigourdan@supelec.fr et kayssar.daher@supelec.fr
 * 
 * Le non-respect de ces termes vous expose à des poursuites judiciaires.
 * 
 */
package com.metzies.ntnoe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.metzies.ntnoe.app.R;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RecupICS extends AsyncTask<URL, String, String> {

	private static File chemin_acces;
	private static File fichier_cible;
	private static URL urlICS;
	private String login;
	private String password;
	private static final int taille_buffer = 8192;
	private boolean telechargement;
	private Context context;
	private static SharedPreferences preflogs;
	public static AlertDialog.Builder builder;
	private boolean update;

	public RecupICS(String login, String password, Context c,
			boolean shouldUpdate) {

		// prends une activité en paramètre, permet de traiter de façon
		// asynchrone et de récupérer un contexte

		// booléen qui permettra ou non le lancement du téléchargement si les
		// vérifs préliminaires sont OK
		telechargement = true;
		this.login = login;
		this.password = password;
		this.context = c;
		update = shouldUpdate;
		preflogs = context.getSharedPreferences("logs", Context.MODE_PRIVATE);
	}

	protected void onPreExecute() {
		// chemin d'accès au fichier

		chemin_acces = new File(preflogs.getString("baseDir",
				Environment.getDataDirectory()
						+ "/data/com.metzies.ntnoe.app/NTNOE/"));
		// chemin d'accès + nom de fichier
		fichier_cible = new File(chemin_acces, "EDT_" + login + ".ics");

		builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
				R.style.AlertDialogCustom));

		// vérifie que le stockage est fonctionnel pour pouvoir enregistrer des
		// fichiers
		String message = verifStockage();
		if (message.length() > 0) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();

		}

		// vérifie que la connexion internet fonctionne
		message = verifConnexion();
		if (message.length() > 0) {
			// Toast.makeText(activity.getApplicationContext(), message,
			// Toast.LENGTH_LONG).show();

			builder.setMessage(
					"Vous n'êtes pas connecté à Internet. Voulez-vous quitter l'appli ou vous connecter?")
					.setCancelable(true)
					.setNegativeButton("Quitter",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Intent.ACTION_MAIN);
									intent.addCategory(Intent.CATEGORY_HOME);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									context.startActivity(intent);
								}
							})
					.setNeutralButton("Se connecter",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									context.startActivity(new Intent(
											android.provider.Settings.ACTION_SETTINGS));

								}
							});

			if (!update) {
				builder.setMessage("Vous n'êtes pas connecté à Internet. Voulez-vous quitter, vous connecter, ou choisir un fichier .ics dans la mémoire du téléphone?");
				builder.setCancelable(false).setPositiveButton(
						"Choisir fichier",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								context.startActivity(new Intent(context,
										FileChooser.class));
							}
						});
			}

			builder.create().show();

			RecupICS.this.cancel(true);

		}
		urlICS = null;
		try {
			urlICS = new URL(
					"https://ntnoe.metz.supelec.fr/ical/EdTcustom/Eleves/edt_"
							+ login + ".ics");

		} catch (MalformedURLException e1) {
			Log.e("Problème", "Problème avec le lien");
			telechargement = false;
			message = "Erreur de lien vers fichier .ics";
			e1.printStackTrace();
		}

	}

	private String verifConnexion() {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected())
			return "";
		telechargement = false;
		return "Erreur de réseau Internet ! !";
	}

	private static String verifStockage() {
		return "";
		/*
		 * String state = Environment.getExternalStorageState(); if
		 * (state.equals(Environment.MEDIA_MOUNTED)) return ""; telechargement =
		 * false; return "Mémoire non disponible (" + state + ")";
		 */
	}

	private static String stockage() {

		// beaucoup de tests écriture/lecture sur mémoire, porte ouverte aux
		// crashs en tous genres
		try {
			// vire le fichier pré-existant
			fichier_cible.delete();

			// vérifie que ça a bien été viré, test en "écriture" de la mémoire
			if (fichier_cible.exists())
				return "Impossible de supprimmer fichier .ics précédent ! ";

			// teste l'écriture mémoire (sert si le fichier était pas //
			// pré-existant, le test ci-dessus le voit pas dans ce cas)

			fichier_cible.getParentFile().mkdirs();

			if (!fichier_cible.createNewFile())
				return "Impossible de créer fichier .ics cible";
			fichier_cible.delete();

			// s'il n'y a aucun soucis, renvoi d'un string vide, qui permettra
			// //
			// que la phase suivante se déroule
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "Stockage impossible sur le téléphone !";
		}

	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	@SuppressLint("TrulyRandom")
	private static void trustAllHosts() {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			// TODO
			// sc.init(null, trustAllCerts, null);
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	/*
	 * interroge le serveur et l'oblige à générer un fichier .ics
	 */
	public void postData() {

		// http://ihofmann.wordpress.com/2013/01/23/android-sending-post-requests-with-parameters/

		// Create a new HttpClient and Post Header

		try {
			URL url = new URL("https://ntnoe.metz.supelec.fr/iCal/index.php");

			String identifiants = login + ":" + password;
			String basicAuth = "Basic "
					+ new String(new Base64().encode(identifiants.getBytes()));

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
                    }
            }, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

            HttpsURLConnection hpost = (HttpsURLConnection) url
					.openConnection();
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("envoyer", "Utf8_All"));
			nameValuePairs.add(new BasicNameValuePair("submit", "Générer"));

            //TODO
            /*
            Ici, l'authentification ne marche pas car Android veut absolument vérifier le CA
            Il faut régler ça
            */
            hpost.setDoOutput(true);
			hpost.setRequestProperty("Authorization", basicAuth);
			hpost.setRequestMethod("POST");
			hpost.setDoInput(true);
            hpost.connect();

			OutputStream os = hpost.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					os, "UTF-8"));
			writer.write(getQuery(nameValuePairs));
			writer.flush();
			writer.close();
			os.close();

			// Execute HTTP Post Request

			hpost.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
            e.printStackTrace();
        }

	}

	@Override
	protected String doInBackground(URL... arg0) {

		if (update) {
			context.startActivity(new Intent(context, Attente.class).putExtra(
					"shouldAskServer", false));
		}

		postData();

		if (telechargement) {
			String dl_fini = "En cours";
			int responseServeur = 0;
			InputStream input = null;
			OutputStream output = null;
			HttpsURLConnection connection = null;

			publishProgress();

			try {
				String stockageOK = stockage();
				if (!stockageOK.isEmpty())
					throw new Exception(stockageOK);

				trustAllHosts();

				// connexion avec l'URL fournie
				connection = (HttpsURLConnection) urlICS.openConnection();

				connection.setHostnameVerifier(DO_NOT_VERIFY);
				// bon ça j'ai mis au pif les 10 sec; mais si on met rien,
				// l'appli poireaute en attendant une réponse qui n'arrive pas
				connection.setConnectTimeout(10000);
				// String identifiants = "nom_utilisateur" + ":" + "mdp";
				String identifiants = login + ":" + password;
				String basicAuth = "Basic "
						+ new String(new Base64().encode(identifiants
								.getBytes()));
				connection.setRequestProperty("Authorization", basicAuth);

				connection.connect();
				// une fois connecté, le serveur renvoie un code de réponse, on
				// regarde si tout est ok (200 = bon; on peut facilement
				// détecter ce qui va pas comme ça, si c'est un 404 not found ou
				// autre)
				responseServeur = connection.getResponseCode();

				if (responseServeur != 200) {

					dl_fini = "Réponse du serveur " + responseServeur + ": "
							+ connection.getResponseMessage();
					return dl_fini;
				}

				Log.d("Réponse serveur", connection.getResponseCode() + "");
				// là on passe à la collecte et à l'écriture des bytes
				input = new BufferedInputStream(connection.getInputStream(),
						taille_buffer);
				output = new FileOutputStream(fichier_cible);

				byte[] buffer = new byte[taille_buffer];

				int byteCount;

				while ((byteCount = input.read(buffer)) != -1) {
					if (isCancelled())
						throw new Exception("Annulation");

					output.write(buffer, 0, byteCount);
				}

				// à partir d'ici, on vérifie qu'il y a pas de soucis, on ferme
				// tout et on choppe toutes les exceptions qui peuvent se
				// produire
				output.close();
				output = null;
				dl_fini = "";

			} catch (UnknownHostException hue) {
				dl_fini = "Hôte inconnu : " + urlICS.getHost();
			} catch (Exception e) {
				Log.e("Erreur", "Exception: ", e);
				dl_fini = "Erreur lors du téléchargement";
				if (responseServeur != 0 && responseServeur != 200)
					dl_fini += "(" + responseServeur + ")";

			} finally {
				if (!dl_fini.isEmpty())
					fichier_cible.delete();
				try {
					if (output != null) {
						output.close();
						output = null;
					}

					if (input != null) {
						input.close();
						input = null;
					}
					if (connection != null) {
						connection.disconnect();
					}
				} catch (IOException e) {

					Log.e("Devik", "IO exception in do in background");
					e.printStackTrace();
				}
			}

			return dl_fini;
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(final String... values) {
		// appelé au début du téléchargement
		Toast.makeText(context, "Téléchargement calendrier en cours...",
				Toast.LENGTH_SHORT).show();
	}

	protected void onPostExecute(String dl_fini) {
		// une fois que tout est fini, on dit à l'utilisateur si tout s'est bien
		// passé ou non
		if (dl_fini.isEmpty()) {

			if (!update) {
				Editor editor = preflogs.edit();
				editor.putString("baseFile", chemin_acces + "/EDT_" + login
						+ ".ics");
				editor.commit();
				/*
				 * try { File direc = new
				 * File(Environment.getExternalStorageDirectory() + "/NTNOE/");
				 * direc.mkdirs(); copy(fichier_cible, new File(direc, "EDT_" +
				 * login + ".ics")); } catch (Exception e) {
				 * e.printStackTrace(); }
				 */
				if (!preflogs.getBoolean("prof", false)) {
					builder.setMessage(
							"Le téléchargement est réussi. Voulez-vous rajouter des créneaux de langues et de sports?")
							.setCancelable(false)
							.setPositiveButton("Oui",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub

											context.startActivity(new Intent(
													context, LangClasses.class)
													.putExtra("update", false));

										}
									})
							.setNegativeButton("Non",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											new ICSAsync(
													context,
													preflogs.getString(
															"baseFile",
															Environment
																	.getDataDirectory()
																	+ "/data/com.metzies.ntnoe.app/NTNOE/"
																	+ "EDT_"
																	+ login
																	+ ".ics"),
													new ArrayList<Event>(), 1)
													.execute();

											context.startActivity(new Intent(
													context, Attente.class)
													.putExtra(
															"shouldAskServer",
															false)
													.setFlags(
															Intent.FLAG_ACTIVITY_NO_ANIMATION));

										}
									});
					builder.create().show();
				} else {

					new ICSAsync(context, preflogs.getString("baseFile",
							Environment.getDataDirectory()
									+ "/data/com.metzies.ntnoe.app/NTNOE/"
									+ "EDT_" + login + ".ics"),
							new ArrayList<Event>(), 1).execute();
					editor.putBoolean("initialized", true);
					editor.commit();
					context.startActivity(new Intent(context, Attente.class)
							.putExtra("shouldAskServer", false).setFlags(
									Intent.FLAG_ACTIVITY_NO_ANIMATION));
				}
			} else if (update) {
				Editor editor = preflogs.edit();
				editor.putString(
						"baseFile",
						preflogs.getString("baseDir",
								Environment.getDataDirectory()
										+ "/data/com.metzies.ntnoe.app/NTNOE/")
								+ "EDT_" + login + ".ics");
				editor.commit();

				try {
					File direc = new File(
							Environment.getExternalStorageDirectory()
									+ "/NTNOE/");
					direc.mkdirs();
					copy(new File(chemin_acces + "/EDT_" + login + ".ics"),
							new File(direc, "EDT_" + login + ".ics"));
				} catch (IOException e) {
					e.printStackTrace();
				}

				new ICSAsync(context.getApplicationContext(),
						preflogs.getString("baseFile", "shit"),
						new Vector<Event>(), 2).execute();

			}

		} else if (dl_fini.contains("401")) {

			// AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(
					"Identifiants incorrects. Veuillez resaisir vos identifiants.")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									context.startActivity(new Intent(context,
											Signup.class));
								}
							});
			builder.create().show();
			/*
			 * Toast.makeText(activity.getApplicationContext(), "Erreur:" +
			 * dl_fini, Toast.LENGTH_LONG).show();
			 */

		} else {
			// AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(
					dl_fini
							+ ". Le téléchargement a échoué. Voulez-vous choisir vous-même un fichier calendrier .ics? Vous pouvez demander à un camarade du même groupe de TD de partager avec vous son EDT grâce à l'option partage.")
					.setCancelable(false)
					.setPositiveButton("Choisir fichier",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									context.startActivity(new Intent(context,
											FileChooser.class));
								}
							})
					.setNegativeButton("Quitter",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Intent.ACTION_MAIN);
									intent.addCategory(Intent.CATEGORY_HOME);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									Editor editor = preflogs.edit();
									editor.clear();
									editor.commit();
									File dir = (new File(Environment
											.getDataDirectory()
											+ "/data/com.metzies.ntnoe/NTNOE/"));
									if (dir.isDirectory()) {
										String[] children = dir.list();
										for (int i = 0; i < children.length; i++) {
											if (children[i].contains("Semaine")
													&& children[i]
															.contains(".ics")) {
												new File(dir, children[i])
														.delete();
											}
										}
									}
									context.startActivity(intent);
								}
							});
			builder.create().show();
		}
	}

	public static void copy(File src, File dst) throws IOException {
		FileInputStream inStream = new FileInputStream(src);
		FileOutputStream outStream = new FileOutputStream(dst);
		FileChannel inChannel = inStream.getChannel();
		FileChannel outChannel = outStream.getChannel();
		inChannel.transferTo(0, inChannel.size(), outChannel);
		inStream.close();
		outStream.close();
	}

}
