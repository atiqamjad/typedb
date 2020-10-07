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
 *
 */

package grakn.core.query;

import grabl.tracing.client.GrablTracingThreadStatic.ThreadTrace;
import grakn.core.common.iterator.ComposableIterator;
import grakn.core.common.parameters.Context;
import grakn.core.common.parameters.Options;
import grakn.core.concept.Concepts;
import grakn.core.concept.answer.ConceptMap;
import grakn.core.concept.type.ThingType;
import grakn.core.graph.Graphs;
import grakn.core.query.executor.Definer;
import grakn.core.query.executor.Deleter;
import grakn.core.query.executor.Inserter;
import grakn.core.query.executor.Matcher;
import grakn.core.query.executor.Undefiner;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlDelete;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlMatch;
import graql.lang.query.GraqlUndefine;

import java.util.List;

import static grabl.tracing.client.GrablTracingThreadStatic.traceOnThread;
import static grakn.core.common.exception.ErrorMessage.Transaction.SESSION_DATA_VIOLATION;
import static grakn.core.common.exception.ErrorMessage.Transaction.SESSION_SCHEMA_VIOLATION;
import static grakn.core.common.iterator.Iterators.iterate;

public class Query {

    private static final String TRACE_PREFIX = "query.";
    private final Graphs graphMgr;
    private final Concepts conceptMgr;
    private final Context.Transaction transactionContext;

    public Query(final Graphs graphMgr, final Concepts conceptMgr, final Context.Transaction transactionContext) {
        this.graphMgr = graphMgr;
        this.conceptMgr = conceptMgr;
        this.transactionContext = transactionContext;
    }

    public ComposableIterator<ConceptMap> match(final GraqlMatch query) {
        return match(query, new Options.Query());
    }

    public ComposableIterator<ConceptMap> match(final GraqlMatch query, final Options.Query options) {
        try (ThreadTrace ignored = traceOnThread(TRACE_PREFIX + "match")) {
            final Context.Query context = new Context.Query(transactionContext, options);
            return Matcher.create(graphMgr, query.conjunction(), context).execute();
        }
    }

    public ComposableIterator<ConceptMap> insert(final GraqlInsert query) {
        return insert(query, new Options.Query());
    }

    public ComposableIterator<ConceptMap> insert(final GraqlInsert query, final Options.Query options) {
        if (transactionContext.sessionType().isSchema()) throw conceptMgr.exception(SESSION_SCHEMA_VIOLATION.message());
        try (ThreadTrace ignored = traceOnThread(TRACE_PREFIX + "insert")) {
            final Context.Query context = new Context.Query(transactionContext, options);
            if (query.match().isPresent()) {
                final List<ConceptMap> matched = match(query.match().get()).toList();
                return iterate(matched).map(answer -> Inserter.create(conceptMgr, query.variables(), answer, context).execute());
            } else {
                return iterate(Inserter.create(conceptMgr, query.variables(), context).execute());
            }
        }
    }

    public void delete(final GraqlDelete query) {
        delete(query, new Options.Query());
    }

    public void delete(final GraqlDelete query, final Options.Query options) {
        if (transactionContext.sessionType().isSchema()) throw conceptMgr.exception(SESSION_SCHEMA_VIOLATION.message());
        try (ThreadTrace ignored = traceOnThread(TRACE_PREFIX + "delete")) {
            final Context.Query context = new Context.Query(transactionContext, options);
            final List<ConceptMap> matched = match(query.match()).toList();
            matched.forEach(existing -> Deleter.create(conceptMgr, query.variables(), existing, context).execute());
        }
    }

    public List<ThingType> define(final GraqlDefine query) {
        return define(query, new Options.Query());
    }

    public List<ThingType> define(final GraqlDefine query, final Options.Query options) {
        if (transactionContext.sessionType().isData()) throw conceptMgr.exception(SESSION_DATA_VIOLATION.message());
        try (ThreadTrace ignored = traceOnThread(TRACE_PREFIX + "define")) {
            final Context.Query context = new Context.Query(transactionContext, options);
            return Definer.create(conceptMgr, query.variables(), context).execute();
        }
    }

    public void undefine(final GraqlUndefine query) {
        undefine(query, new Options.Query());
    }

    public void undefine(final GraqlUndefine query, final Options.Query options) {
        if (transactionContext.sessionType().isData()) throw conceptMgr.exception(SESSION_DATA_VIOLATION.message());
        try (ThreadTrace ignored = traceOnThread(TRACE_PREFIX + "undefine")) {
            final Context.Query context = new Context.Query(transactionContext, options);
            Undefiner.create(conceptMgr, query.variables(), context).execute();
        }
    }
}
