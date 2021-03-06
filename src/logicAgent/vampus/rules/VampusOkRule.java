package logicAgent.vampus.rules;

import logicAgent.vampus.Bool;
import logicAgent.vampus.CellInfo;
import logicAgent.vampus.VampusAgentMove;
import logicAgent.vampus.VampusSensors;

public class VampusOkRule extends VampusAbstractRule {

    public VampusOkRule(CellInfo[][] cellsInfo, Bool foundNewOk) {
        super(cellsInfo, foundNewOk);
    }

    @Override
    protected void concreteConclude(int row, int col, VampusSensors sensors, VampusAgentMove.Type prevMove) {
        if (!sensors.isBreeze() && !sensors.isStench()) {
            getCell(row - 1, col).ifPresent(this::setOkNeighbor);
            getCell(row + 1, col).ifPresent(this::setOkNeighbor);
            getCell(row, col - 1).ifPresent(this::setOkNeighbor);
            getCell(row, col + 1).ifPresent(this::setOkNeighbor);
        }
    }

    private void setOkNeighbor(CellInfo cellInfo) {
        if(cellInfo.isOk()){
            return;
        }
        if (cellInfo.getWall() != CellInfo.Type.TRUE) {
            cellInfo.setOk(CellInfo.Type.TRUE);
            this.foundNewOk.setValue(true);
        }
        cellInfo.setHole(CellInfo.Type.FALSE);
        cellInfo.setVampus(CellInfo.Type.FALSE);
    }
}
