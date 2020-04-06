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

package hypergraph.concept.type;

import hypergraph.common.exception.HypergraphException;
import hypergraph.graph.Graph;
import hypergraph.graph.Schema;
import hypergraph.graph.vertex.TypeVertex;

import java.util.HashSet;
import java.util.Set;

public class RelationType extends ThingType<RelationType> {

    private final Set<RoleType> roleTypes;

    public static RelationType of(TypeVertex vertex) {
        if (vertex.label().equals(Schema.Vertex.Type.Root.RELATION.label())) return new RelationType.Root(vertex);
        else return new RelationType(vertex);
    }

    public static RelationType of(Graph.Type graph, String label) {
        return new RelationType(graph, label);
    }

    private RelationType(TypeVertex vertex) {
        super(vertex);
        roleTypes = new HashSet<>();
        if (vertex.schema() != Schema.Vertex.Type.RELATION_TYPE) {
            throw new HypergraphException("Invalid Relation Type: " + vertex.label() +
                                                  " subtypes " + vertex.schema().root().label());
        }
    }

    private RelationType(Graph.Type graph, String label) {
        super(graph, label, Schema.Vertex.Type.RELATION_TYPE);
        roleTypes = new HashSet<>();
    }

    @Override
    RelationType newInstance(TypeVertex vertex) {
        return of(vertex);
    }

    public static class Root extends RelationType {

        Root(TypeVertex vertex) {
            super(vertex);
            assert vertex.label().equals(Schema.Vertex.Type.Root.RELATION.label());
        }

        @Override
        public void label(String label) {
            throw new HypergraphException("Invalid Operation Exception: root types are immutable");
        }

        @Override
        public void setAbstract(boolean isAbstract) {
            throw new HypergraphException("Invalid Operation Exception: root types are immutable");
        }

        @Override
        public RelationType sup() {
            return null;
        }

        @Override
        public void sup(RelationType superType) {
            throw new HypergraphException("Invalid Operation Exception: root types are immutable");
        }
    }
}
