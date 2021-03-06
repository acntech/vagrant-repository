import {
    Box,
    BoxState,
    BoxAction,
    CreateBoxAction,
    CreateBoxActionType,
    DeleteBoxAction,
    DeleteBoxActionType,
    GetBoxAction,
    GetBoxActionType,
    FindBoxesAction,
    FindBoxesActionType,
    UpdateBoxAction,
    UpdateBoxActionType,
    EntityType,
    ActionType
} from '../../models';
import { initialBoxState } from '../store/initial-state';

export const reducer = (state: BoxState = initialBoxState, action: BoxAction): BoxState => {
    switch (action.type) {
        case CreateBoxActionType.LOADING:
        case CreateBoxActionType.SUCCESS:
        case CreateBoxActionType.ERROR:
            return createBox(state, action);
        case DeleteBoxActionType.LOADING:
        case DeleteBoxActionType.SUCCESS:
        case DeleteBoxActionType.ERROR:
            return deleteBox(state, action);
        case FindBoxesActionType.LOADING:
        case FindBoxesActionType.SUCCESS:
        case FindBoxesActionType.ERROR:
            return findBoxes(state, action);
        case GetBoxActionType.LOADING:
        case GetBoxActionType.SUCCESS:
        case GetBoxActionType.ERROR:
            return getBox(state, action);
        case UpdateBoxActionType.LOADING:
        case UpdateBoxActionType.SUCCESS:
        case UpdateBoxActionType.ERROR:
            return updateBox(state, action);
        default:
            return state;
    }
};

const createBox = (state: BoxState = initialBoxState, action: CreateBoxAction): BoxState => {
    switch (action.type) {
        case CreateBoxActionType.LOADING: {
            const { boxes } = state;
            const { loading } = action;
            return { ...initialBoxState, boxes: boxes, loading: loading };
        }

        case CreateBoxActionType.SUCCESS: {
            let { boxes } = state;
            const { payload } = action;
            let modified;

            if (payload) {
                boxes = replaceOrAppend(boxes, payload);
                modified = { id: payload.id, entityType: EntityType.BOX, actionType: ActionType.CREATE };
            }

            return { ...initialBoxState, boxes: boxes, modified: modified };
        }

        case CreateBoxActionType.ERROR: {
            const { boxes } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.BOX, actionType: ActionType.CREATE };
            return { ...initialBoxState, boxes: boxes, error: error };
        }

        default: {
            return state;
        }
    }
};

const deleteBox = (state: BoxState = initialBoxState, action: DeleteBoxAction): BoxState => {
    switch (action.type) {
        case DeleteBoxActionType.LOADING: {
            const { boxes } = state;
            const { loading } = action;
            return { ...initialBoxState, boxes: boxes, loading: loading };
        }

        case DeleteBoxActionType.SUCCESS: {
            let { boxes } = state;
            const { boxId } = action;
            let modified = { id: boxId, entityType: EntityType.BOX, actionType: ActionType.DELETE };
            boxes = boxes.filter(box => box.id != boxId);

            return { ...initialBoxState, boxes: boxes, modified: modified };
        }

        case DeleteBoxActionType.ERROR: {
            const { boxes } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.BOX, actionType: ActionType.DELETE };
            return { ...initialBoxState, boxes: boxes, error: error };
        }

        default: {
            return state;
        }
    }
};

const findBoxes = (state: BoxState = initialBoxState, action: FindBoxesAction): BoxState => {
    switch (action.type) {
        case FindBoxesActionType.LOADING: {
            const { boxes } = state;
            const { loading } = action;
            return { ...initialBoxState, boxes: boxes, loading: loading };
        }

        case FindBoxesActionType.SUCCESS: {
            let { boxes } = state;
            const { payload } = action;

            if (payload) {
                payload.forEach(box => {
                    boxes = replaceOrAppend(boxes, box);
                });
            }

            return { ...initialBoxState, boxes: boxes, modified: undefined };
        }

        case FindBoxesActionType.ERROR: {
            const { boxes } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.BOX, actionType: ActionType.FIND };
            return { ...initialBoxState, boxes: boxes, error: error };
        }

        default: {
            return state;
        }
    }
};

const getBox = (state: BoxState = initialBoxState, action: GetBoxAction): BoxState => {
    switch (action.type) {
        case GetBoxActionType.LOADING: {
            const { boxes } = state;
            const { loading } = action;
            return { ...initialBoxState, boxes: boxes, loading: loading };
        }

        case GetBoxActionType.SUCCESS: {
            let { boxes } = state;
            const { payload } = action;

            if (payload) {
                boxes = replaceOrAppend(boxes, payload);
            }

            return { ...initialBoxState, boxes: boxes };
        }

        case GetBoxActionType.ERROR: {
            const { boxes } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.BOX, actionType: ActionType.GET };
            return { ...initialBoxState, boxes: boxes, error: error };
        }

        default: {
            return state;
        }
    }
};

const updateBox = (state: BoxState = initialBoxState, action: UpdateBoxAction): BoxState => {
    switch (action.type) {
        case UpdateBoxActionType.LOADING: {
            const { boxes } = state;
            const { loading } = action;
            return { ...initialBoxState, boxes: boxes, loading: loading };
        }

        case UpdateBoxActionType.SUCCESS: {
            let { boxes } = state;
            const { payload } = action;
            let modified;

            if (payload) {
                boxes = replaceOrAppend(boxes, payload);
                modified = { id: payload.id, entityType: EntityType.BOX, actionType: ActionType.UPDATE };
            }

            return { ...initialBoxState, boxes: boxes, modified: modified };
        }

        case UpdateBoxActionType.ERROR: {
            const { boxes } = state;
            const { data } = action.error.response;
            const error = { ...data, entityType: EntityType.BOX, actionType: ActionType.UPDATE };
            return { ...initialBoxState, boxes: boxes, error: error };
        }

        default: {
            return state;
        }
    }
};

const replaceOrAppend = (boxes: Box[], box: Box) => {
    const index = boxes.map(box => box.id).indexOf(box.id);

    if (~index) {
        boxes[index] = box;
    } else {
        boxes = boxes.concat(box);
    }

    return boxes;
};