package logicAgent.vampus.rules;

import logicAgent.vampus.*;

import java.util.Optional;


public abstract class VampusAbstractRule {

    protected final CellInfo[][] cellsInfo;
    protected final Bool foundNewOk;

    public VampusAbstractRule(CellInfo[][] cellsInfo, Bool foundNewOk) {
        this.cellsInfo = cellsInfo;
        this.foundNewOk = foundNewOk;
    }

    public void conclude(int row, int col, VampusSensors sensors, VampusAgentMove.Type prevMove) {
        concreteConclude(row, col, sensors, prevMove);
    }

    protected abstract void concreteConclude(int row, int col, VampusSensors sensors, VampusAgentMove.Type prevMove);

    protected Integer getUp(Integer row){
        if(row == null){
            return null;
        }
        if(row <= 0){
            return null;
        } else {
            return row - 1;
        }
    }

    protected Integer getDown(Integer row){
        if(row == null){
            return null;
        }
        if(row >= VampusGame.HEIGHT - 1){
            return null;
        } else {
            return row + 1;
        }
    }

    protected Integer getLeft(Integer col){
        if(col == null){
            return null;
        }
        if(col <= 0){
            return null;
        } else {
            return col - 1;
        }
    }

    protected Integer getRight(Integer col){
        if(col == null){
            return null;
        }
        if(col >= VampusGame.WIDTH - 1){
            return null;
        } else {
            return col + 1;
        }
    }

    protected Optional<CellInfo> getCell(int row, int col) {
        if (row < 0 || row >= VampusGame.HEIGHT || col < 0 || col >= VampusGame.WIDTH) {
            return Optional.empty();
        } else {
            return Optional.of(cellsInfo[row][col]);
        }
    }

}
