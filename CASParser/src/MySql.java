import config.ConfigFactory;
import config.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parsing.dbdump.XMLParser;
import structures.Compound;
import structures.Tuple;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jason on 2016/10/26.
 */
public class MySql {

	public static final Logger logger = LogManager.getLogger(MySql.class);
	private static AtomicLong numberOfQueries = new AtomicLong(0);
	protected Connection connection;
	protected DatabaseConfig config = (DatabaseConfig) ConfigFactory.getConfig(ConfigFactory.CONFIG_TYPE.DB);
	protected String lastQuery;

	public MySql() {
		try {
			int port = config.port < 1 ? 3306 : config.port;
			String url = String.format("jdbc:mysql://%s:%d/%s%s",
					config.address, port, config.name, config.extras);
			logger.info(url);

			connection = DriverManager.getConnection(url, config.user, config.password);
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			logger.info("Successfully connected to the db");
		} catch(Exception e) {
			logger.fatal("Cannot connect to the Database: " + e.toString());
			System.exit(0);
		}
	}

	public static long getNumberOfQueries() {
		return numberOfQueries.get();
	}

	public boolean insertCompound(Compound c) throws SQLException {

		if (recordExists(c)) {
			return true;
		}

		PreparedStatement chemicals = null;
		PreparedStatement inchi = null;
		PreparedStatement cas = null;
		PreparedStatement synonyms = null;
		PreparedStatement molecularData = null;
		PreparedStatement reactiveGroup = null;
		PreparedStatement formulas = null;
		PreparedStatement smiles = null;
		PreparedStatement unNumbers = null;

		long id = -1;
		long time = System.currentTimeMillis() / 1000;

		try {
			chemicals = connection.prepareStatement("INSERT INTO " +
					"chemicals(pubchem_id, name, last_modified) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

			chemicals.setInt(1, c.getPubChem());
			chemicals.setString(2, c.getName());
			chemicals.setLong(3, time);

			int rows = chemicals.executeUpdate();
			ResultSet chemRS = chemicals.getGeneratedKeys();
			if (chemRS.first()) {
				id = chemRS.getLong(1);
			}
			connection.commit();
			numberOfQueries.incrementAndGet();
		} catch(SQLException e) {
			throwError(e, chemicals, "chemical");
			XMLParser.saveErroredCompound(c);
			return false;
		}

		try {
			inchi = connection.prepareStatement("INSERT INTO inchi(chemicals_id, inchi, inchi_key) VALUES(?, ?, ?)");
			inchi.setLong(1, id);
			inchi.setString(2, c.getInchi());
			inchi.setString(3, c.getInchiKey());
			inchi.executeUpdate();
			connection.commit();
			numberOfQueries.incrementAndGet();
		} catch(SQLException e) {
			throwError(e, inchi, "inchi");
		}

		try {
			if (c.isHasCas()) {
				for (Tuple<Integer, String> s : c.getCasNumber()) {
					//cas = connection.prepareStatement("INSERT INTO cas_numbers(chemicals_id, cas_number_string, " +
					//		"cas_number_number) VALUES(?, ?, ?)");
					cas = connection.prepareStatement("INSERT INTO cas_numbers(chemicals_id, cas_number_string) " +
							"VALUES (?, ?)");
					cas.setLong(1, id);
					cas.setString(2, s.getRight());
					//cas.setInt(3, s.getLeft());
					cas.executeUpdate();
					connection.commit();
					numberOfQueries.incrementAndGet();
				}
			}
		} catch(SQLException e){
			throwError(e, cas, "cas");
		}

		try {
			if (c.isHasSynonyms()) {
				for (String s : c.getSynoynms()) {
					synonyms = connection.prepareStatement("INSERT INTO mesh_synonyms(chemicals_id, name) VALUES (?, ?)");
					synonyms.setLong(1, id);
					synonyms.setString(2, s);
					synonyms.executeUpdate();
					connection.commit();
					numberOfQueries.incrementAndGet();
				}
			}
		} catch (SQLException e) {
			throwError(e, synonyms, "synonyms");
		}

		try {
			molecularData = connection.prepareStatement("INSERT INTO molecular_data (chemicals_id, " +
					"molecular_weight, exact_mass, hydrogen_donor, hydrogen_acceptor, rotatable_count, " +
					"complexity) VALUES (?, ?, ?, ?, ?, ?, ?)");
			molecularData.setLong(1, id);
			molecularData.setDouble(2, c.getMolecularWeight());
			molecularData.setDouble(3, c.getExactMass());
			molecularData.setInt(4, c.getDonorBonds());
			molecularData.setInt(5, c.getAcceptorBonds());
			molecularData.setInt(6, c.getRotatableBonds());
			molecularData.setDouble(7, c.getComplexity());
			molecularData.executeUpdate();
			connection.commit();
			numberOfQueries.incrementAndGet();
		} catch(SQLException e) {
			throwError(e, molecularData, "molecular data");
		}

		try {
			if (c.isHasGroup()) {
				for (int i : c.getGroup()) {
					reactiveGroup = connection.prepareStatement("INSERT INTO chemicals_reactive_groups (chemicals_id, " +
							"reactive_groups_id) VALUES (?, ?)");
					reactiveGroup.setLong(1, id);
					reactiveGroup.setInt(2, i);
					reactiveGroup.executeUpdate();
					connection.commit();
					numberOfQueries.incrementAndGet();
				}
			}
		} catch(SQLException e) {
			throwError(e, reactiveGroup, "reactive groups");
		}

		/*try {
			if (c.isHasMolecularFormulas()) {
				for (String s : c.getFormula()) {
					formulas = connection.prepareStatement("INSERT INTO molecular_formulas(chemicals_id, formula) " +
							"VALUES (?, ?)");
					formulas.setLong(1, id);
					formulas.setString(2, s);
					formulas.executeUpdate();
					connection.commit();
					numberOfQueries.incrementAndGet();
				}
			}
		} catch(SQLException e) {
			throwError(e, formulas, "formulas");
		}

		try {
			if (c.isHasSmiles()) {
				for (String s : c.getSmiles()) {
					smiles = connection.prepareStatement("INSERT INTO smiles(chemicals_id, smiles) " +
							"VALUES (?, ?)");
					smiles.setLong(1, id);
					smiles.setString(2, s);
					smiles.executeUpdate();
					connection.commit();
					numberOfQueries.incrementAndGet();
				}
			}
		} catch(SQLException e) {
			throwError(e, smiles, "smiles");
		}*/

		try {
			if (c.isHasUnNumbers()) {
				for (int i : c.getUnNumber()) {
					unNumbers = connection.prepareStatement("");
					unNumbers.setLong(1, id);
					unNumbers.setInt(2, i);
					unNumbers.executeUpdate();
					connection.commit();
					numberOfQueries.incrementAndGet();
				}
			}
		} catch(SQLException e) {
			throwError(e, unNumbers, "un");
		} finally {
			if (chemicals != null) {
				chemicals.close();
			}
			if (inchi != null) {
				inchi.close();
			}
			if (cas != null) {
				cas.close();
			}
			if (synonyms != null) {
				synonyms.close();
			}
			if (molecularData != null) {
				molecularData.close();
			}
			if (reactiveGroup != null) {
				reactiveGroup.close();
			}
			if (formulas != null) {
				formulas.close();
			}
			if (smiles != null) {
				smiles.close();
			}
			if (unNumbers != null) {
				unNumbers.close();
			}
		}
		return true;
	}

	private boolean recordExists(Compound c) {
		try {
			PreparedStatement s = connection.prepareStatement("SELECT id FROM chemicals WHERE pubchem_id = ?");

			s.setInt(1, c.getPubChem());
			ResultSet rs = s.executeQuery();
			return rs.first();
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean insertReactiveGroup(long cid, int rid) {
		boolean result = false;
		Set<Long> inserted = new HashSet<Long>();
		try {
			PreparedStatement select = connection.prepareStatement("SELECT pubchem_id FROM chemicals_reactive_groups " +
					"WHERE pubchem_id = ? and reactive_groups_id = ?");
			select.setLong(1, cid);
			select.setInt(2, rid);

			ResultSet rs = select.executeQuery();

			if (!rs.first()) {
				PreparedStatement insert = connection.prepareStatement("INSERT INTO " +
						"chemicals_reactive_groups(pubchem_id, reactive_groups_id) VALUES(?, ?)");
				insert.setLong(1, cid);
				insert.setInt(2, rid);
				int rows = insert.executeUpdate();
				connection.commit();
				if (rows > 0) {
					result = true;
				} else {
					logger.error("error inserting cid: " + cid + "\t rid: " + rid);
				}
			}
		} catch (SQLException e) {
			logger.error(e.toString());
		}
		return result;
	}

	private void throwError(Exception e, PreparedStatement p, String who) {
		logger.error(who + "\n" + p.toString() + "\n" + e.toString());
		// close and rollback
		try {connection.rollback();p.close();} catch(SQLException a) {}
	}

	public void closeConnection(){
		try {
			this.connection.close();
		} catch(SQLException e) {
			logger.error(e.toString());
		}
	}
}
