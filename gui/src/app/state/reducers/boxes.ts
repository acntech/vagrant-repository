import {
    Box,
    BoxState,
    BoxAction,
    CreateBoxAction,
    CreateBoxActionType,
    GetBoxAction,
    GetBoxActionType,
    FindBoxesAction,
    FindBoxesActionType,
    EntityType,
    ActionType
} from '../../models';
import { initialBoxState } from '../store';

export const reducer = (state: BoxState = initialBoxState, action: BoxAction): BoxState => {
    switch (action.type) {
        case CreateBoxActionType.LOADING:
        case CreateBoxActionType.SUCCESS:
        case CreateBoxActionType.ERROR:
            return create(state, action);
        case GetBoxActionType.LOADING:
        case GetBoxActionType.SUCCESS:
        case GetBoxActionType.ERROR:
            return get(state, action);
        case FindBoxesActionType.LOADING:
        case FindBoxesActionType.SUCCESS:
        case FindBoxesActionType.ERROR:
            return find(state, action);
        default:
            return state;
    }
};

export const create = (state: BoxState = initialBoxState, action: CreateBoxAction): BoxState => {
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

export const get = (state: BoxState = initialBoxState, action: GetBoxAction): BoxState => {
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

export const find = (state: BoxState = initialBoxState, action: FindBoxesAction): BoxState => {
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
            const error = { ...data, entityType: EntityType.BOX, actionType: ActionType.GET };
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