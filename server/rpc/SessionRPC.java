/*
 * Copyright (C) 2020 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grakn.core.server.rpc;

import grakn.core.Grakn;
import grakn.core.common.options.GraknOptions;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRPC {

    private final Grakn.Session session;
    private final Set<TransactionRPC> transactionRPCs;

    SessionRPC(Grakn grakn, String database, Grakn.Session.Type type, GraknOptions.Session options) {
        this.session = grakn.session(database, type, options);
        transactionRPCs = ConcurrentHashMap.newKeySet();
    }

    Grakn.Session session() {
        return session;
    }

    Grakn.Transaction transaction(TransactionRPC transactionRPC) {
        transactionRPCs.add(transactionRPC);
        return session.transaction(transactionRPC.type(), transactionRPC.options());
    }

    void onError(Throwable error) {
        transactionRPCs.forEach(ts -> ts.close(error));
        session.close();
    }

    void remove(TransactionRPC transactionRPC) {
        transactionRPCs.remove(transactionRPC);
    }

    void close(@Nullable Throwable error) {
        transactionRPCs.forEach(ts -> ts.close(error));
        session.close();
    }
}
