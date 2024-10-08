/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typedb.core.pattern.constraint.type;

import com.vaticle.typedb.core.common.iterator.FunctionalIterator;
import com.vaticle.typedb.core.common.parameters.Label;
import com.vaticle.typedb.core.pattern.Conjunction;
import com.vaticle.typedb.core.pattern.equivalence.AlphaEquivalence;
import com.vaticle.typedb.core.pattern.equivalence.AlphaEquivalent;
import com.vaticle.typedb.core.pattern.variable.TypeVariable;
import com.vaticle.typedb.core.traversal.GraphTraversal;

import java.util.Objects;
import java.util.Optional;

import static com.vaticle.typedb.common.collection.Collections.set;
import static com.vaticle.typeql.lang.common.TypeQLToken.Char.SPACE;
import static com.vaticle.typeql.lang.common.TypeQLToken.Constraint.TYPE;

public class LabelConstraint extends TypeConstraint implements AlphaEquivalent<LabelConstraint> {

    private final Label label;
    private final int hash;

    public LabelConstraint(TypeVariable owner, Label label) {
        super(owner, set());
        if (label == null) throw new NullPointerException("Null label");
        this.label = label;
        this.hash = Objects.hash(LabelConstraint.class, this.owner, this.label);
    }

    static LabelConstraint of(TypeVariable owner, com.vaticle.typeql.lang.pattern.constraint.TypeConstraint.Label constraint) {
        return new LabelConstraint(owner, Label.of(constraint.label(), constraint.scope().orElse(null)));
    }

    static LabelConstraint of(TypeVariable owner, LabelConstraint clone) {
        return new LabelConstraint(owner, Label.of(clone.label(), clone.scope().orElse(null)));
    }

    public Optional<String> scope() {
        return label.scope();
    }

    public String label() {
        return label.name();
    }

    public String scopedLabel() {
        return label.scopedName();
    }

    public Label properLabel() {
        return label;
    }

    @Override
    public void addTo(GraphTraversal.Thing traversal) {
        assert !owner.inferredTypes().isEmpty();
    }

    @Override
    public boolean isLabel() {
        return true;
    }

    @Override
    public LabelConstraint asLabel() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelConstraint that = (LabelConstraint) o;
        return this.owner.equals(that.owner) && this.label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return owner.toString() + SPACE + TYPE + SPACE + scopedLabel();
    }

    @Override
    public FunctionalIterator<AlphaEquivalence> alphaEquals(LabelConstraint that) {
        return owner.alphaEquals(that.owner)
                .flatMap(a -> a.alphaEqualIf(label().equals(that.label())));
    }

    @Override
    public LabelConstraint clone(Conjunction.ConstraintCloner cloner) {
        return cloner.cloneVariable(owner).label(label);
    }
}
