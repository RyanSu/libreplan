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

package org.navalplanner.business.planner.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.hibernate.validator.AssertTrue;

/**
 * @author Óscar González Fernández <ogonzalez@igalia.com>
 */
public class TaskGroup extends TaskElement {

    public static TaskGroup create() {
        TaskGroup taskGroup = new TaskGroup();
        taskGroup.setNewObject(true);
        return taskGroup;
    }

    private List<TaskElement> taskElements = new ArrayList<TaskElement>();

    /**
     * Constructor for hibernate. Do not use!
     */
    public TaskGroup() {

    }

    @SuppressWarnings("unused")
    @AssertTrue(message = "order element associated to a task group must be not null")
    private boolean theOrderElementMustBeNotNull() {
        return getOrderElement() != null;
    }

    public void addTaskElement(TaskElement task) {
        addTaskElement(taskElements.size(), task);
    }

    public void addTaskElement(Integer index, TaskElement task) {
        Validate.notNull(task);
        task.setParent(this);
        taskElements.add(index, task);
    }

    @Override
    public List<TaskElement> getChildren() {
        return Collections.unmodifiableList(taskElements);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Integer defaultWorkHours() {
        return getOrderElement().getWorkHours();
    }

    public void remove(TaskElement taskElement) {
        taskElements.remove(taskElement);
    }

    @Override
    public Set<ResourceAllocation<?>> getResourceAllocations() {
        Set<ResourceAllocation<?>> result = new HashSet<ResourceAllocation<?>>();

        List<TaskElement> children = this.getChildren();
        for (TaskElement child : children) {
            result.addAll(child.getResourceAllocations());
        }

        return result;
    }

    @Override
    protected void moveAllocations() {
        // do nothing
    }
}
