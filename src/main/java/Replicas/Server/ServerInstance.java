package Replicas.Server;

import Replicas.Server.Constant.ServerConstant;
import Replicas.Server.Log.ILogging;
import Replicas.Server.Log.Logging;
import Replicas.Server.Shared.Database.CustomerBooking;
import Replicas.Server.Shared.Database.ICustomerBooking;
import Replicas.Server.Shared.Database.IMovies;
import Replicas.Server.Shared.Database.Movies;
import Replicas.Server.Shared.data.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerInstance {
    public ServerInstance() {}
    public static void main(String[] args) {

            IServerInfo serverInfo;
            IUdp udpService;
            IMovie movieService;
            ICustomerBooking customerBookingDb;
            IMovies moviesDb;
            ILogging logging;
            try {
                Logger logger = Logger.getLogger(Util.getServerNameByServerPrefix(ServerConstant.SERVER_ATWATER_PREFIX));
                serverInfo = new ServerInfo();
                udpService = new Udp();
                movieService = new Movie();
                customerBookingDb = new CustomerBooking();
                moviesDb = new Movies();
                moviesDb.addMovie("AVATAR", "ATWM120423", 50);
                //moviesDb.addMovieSlot("AVATAR", "ATWA210323", 50);
                moviesDb.addMovieSlot("AVATAR", "ATWA140423", 50);
                moviesDb.addMovieSlot("AVATAR", "ATWA150423", 50);
                moviesDb.addMovieSlot("AVATAR", "ATWA130423", 50);
                moviesDb.addMovie("AVENGERS", "ATWE160423", 100);
                customerBookingDb.addMovieByCustomerID("ATWM1212", "ATWM120423", "AVATAR", 3);
                customerBookingDb.addMovieByCustomerID("ATWM1212", "ATWA150423", "AVATAR", 3);
                serverInfo.setServerName(ServerConstant.SERVER_ATWATER_PREFIX);
                logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()), false, true);
                logger = logging.attachFileHandlerToLogger(logger);
                Server Atwater = new Server(logger, ServerConstant.SERVER_ATWATER_PREFIX, serverInfo, udpService, movieService, customerBookingDb, moviesDb, args);
                Atwater.setPriority(1);
                Atwater.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                Logger logger = Logger.getLogger(Util.getServerNameByServerPrefix(ServerConstant.SERVER_VERDUN_PREFIX));
                serverInfo = new ServerInfo();
                udpService = new Udp();
                movieService = new Movie();
                customerBookingDb = new CustomerBooking();
                moviesDb = new Movies();
                moviesDb.addMovie("AVATAR", "VERM220323", 30);
                moviesDb.addMovie("AVENGERS", "VERM230323", 40);
                moviesDb.addMovie("TITANIC", "VERM260323", 40);
                customerBookingDb.addMovieByCustomerID("VERM1212","VERA220323","AVATAR",23);
                serverInfo.setServerName(ServerConstant.SERVER_VERDUN_PREFIX);
                logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()), false, true);
                logger = logging.attachFileHandlerToLogger(logger);
                Server Verdun = new Server(logger, ServerConstant.SERVER_VERDUN_PREFIX, serverInfo, udpService, movieService, customerBookingDb, moviesDb, args);
                Verdun.setPriority(2);
                Verdun.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                Logger logger = Logger.getLogger(Util.getServerNameByServerPrefix(ServerConstant.SERVER_OUTREMONT_PREFIX));
                serverInfo = new ServerInfo();
                udpService = new Udp();
                movieService = new Movie();
                customerBookingDb = new CustomerBooking();
                moviesDb = new Movies();
                moviesDb.addMovie("TITANIC","OUTM240323",30);
                moviesDb.addMovie("AVATAR","OUTE210323",30);
                serverInfo.setServerName(ServerConstant.SERVER_OUTREMONT_PREFIX);
                logging = new Logging(Util.getServerNameByServerPrefix(serverInfo.getServerName()), false, true);
                logger = logging.attachFileHandlerToLogger(logger);
                Server Outremont = new Server(logger, ServerConstant.SERVER_OUTREMONT_PREFIX, serverInfo, udpService, movieService, customerBookingDb, moviesDb, args);
                Outremont.setPriority(3);
                Outremont.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
}
