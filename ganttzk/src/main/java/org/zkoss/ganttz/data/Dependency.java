/*
 * This file is part of ###PROJECT_NAME###
 *
 * Copyright (C) 2009 Fundación para o Fomento da Calidade Industrial e
 *                    Desenvolvemento Tecnolóxico de Galicia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.zkoss.ganttz.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.zkoss.ganttz.data.constraint.Constraint;

/**
 * This class represents a dependency. Contains the source and the destination.
 * It also specifies the type of the relationship. <br/>
 * @author Óscar González Fernández <ogonzalez@igalia.com>
 */
public class Dependency {

    private enum Calculation {
        START {
            @Override
            public List<Constraint<Date>> toConstraints(Task source,
                    DependencyType type) {
                return type.getStartConstraints(source);
            }
        },
        END {
            @Override
            public List<Constraint<Date>> toConstraints(Task source,
                    DependencyType type) {
                return type.getEndConstraints(source);
            }
        };

        abstract List<Constraint<Date>> toConstraints(Task source,
                DependencyType type);
    }

    public static List<Constraint<Date>> getStartConstraints(
            Collection<Dependency> dependencies) {
        return getConstraintsFor(dependencies, Calculation.START);
    }

    public static List<Constraint<Date>> getEndConstraints(
            Collection<Dependency> incoming) {
        return getConstraintsFor(incoming, Calculation.END);
    }

    private static List<Constraint<Date>> getConstraintsFor(
            Collection<Dependency> dependencies, Calculation calculation) {
        List<Constraint<Date>> result = new ArrayList<Constraint<Date>>();
        for (Dependency dependency : dependencies) {
            result.addAll(dependency.toConstraints(calculation));
        }
        return result;
    }

    public static Date calculateStart(Task origin, Date current,
            Collection<? extends Dependency> dependencies) {
        return apply(Calculation.START, origin, current, dependencies);
    }

    public static Date calculateEnd(Task origin, Date current,
            Collection<? extends Dependency> depencencies) {
        return apply(Calculation.END, origin, current, depencencies);
    }


    private static Date apply(Calculation calculation, Task origin,
            Date current, Collection<? extends Dependency> dependencies) {
        Date result = null;
        for (Dependency dependency : dependencies) {
            switch (calculation) {
            case START:
                result = dependency.getType().calculateStartDestinyTask(
                        dependency.getSource(), current);
                break;
            case END:
                result = dependency.getType().calculateEndDestinyTask(
                        dependency.getSource(), current);
                break;
            default:
                throw new RuntimeException("unexpected calculation "
                        + calculation);
            }
        }
        return result;
    }

    private List<Constraint<Date>> toConstraints(
            Calculation calculation) {
        return calculation.toConstraints(source, type);
    }

    private final Task source;

    private final Task destination;

    private DependencyType type;

    private final boolean visible;

    public Dependency(Task source, Task destination,
            DependencyType type, boolean visible) {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("destination cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.visible = visible;
    }

    public Dependency(Task source, Task destination,
            DependencyType type) {
        this(source, destination, type, true);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(source).append(destination).append(
                type).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Dependency other = (Dependency) obj;
        return new EqualsBuilder().append(this.destination, other.destination)
                .append(this.source, other.source)
                .append(this.type, other.type).isEquals();
    }

    public Task getSource() {
        return source;
    }

    public Task getDestination() {
        return destination;
    }

    public DependencyType getType() {
        return type;
    }

    public boolean isVisible() {
        return visible;
    }

    public Dependency createWithType(DependencyType type) {
        return new Dependency(source, destination, type, visible);
    }

}