/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;

/**
 * @author Olga Melnichuk
 */
public class MoveProtocolCommand implements ExperimentUpdateCommand {

    static enum Direction {
        UP {
            @Override
            void apply(ExperimentUpdatePerformer performer, ProtocolRow row) {
                performer.moveProtocolUp(row);
            }
        },
        DOWN {
            @Override
            void apply(ExperimentUpdatePerformer performer, ProtocolRow row) {
                performer.moveProtocolDown(row);
            }
        };

        abstract void apply(ExperimentUpdatePerformer performer, ProtocolRow row);
    }

    private Direction direction;

    private ProtocolRow row;

    MoveProtocolCommand() {
        /*Used by GWT serialization*/
    }

    private MoveProtocolCommand(ProtocolRow row, Direction direction) {
        this.direction = direction;
        this.row = row;
    }

    @Override
    public void execute(ExperimentUpdatePerformer performer) {
        direction.apply(performer, row);
    }

    @Override
    public boolean isCritical() {
        return false;
    }

    public static MoveProtocolCommand down(ProtocolRow row) {
        return new MoveProtocolCommand(row, Direction.DOWN);
    }

    public static MoveProtocolCommand up(ProtocolRow row) {
        return new MoveProtocolCommand(row, Direction.UP);
    }
}
