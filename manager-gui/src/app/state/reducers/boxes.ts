import {
    Box,
    BoxState,
    BoxAction,
    CreateBoxAction,
    CreateBoxActionType,
    GetBoxAction,
    GetBoxActionType,
    FindBoxesAction,
    FindBoxesActionType
} from '../../models';
import { initialBoxState } from '../store/initial-state';

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
}

export const create = (state: BoxState = initialBoxState, action: CreateBoxAction): BoxState => {
    switch (action.type) {
        case CreateBoxActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
        }

        case CreateBoxActionType.SUCCESS: {
            const { payload } = action;
            let { boxes } = state;
            let modified;

            if (payload) {
                boxes = replaceOrAppend(boxes, payload);
                modified = { id: payload.id };
            }

            return { ...initialBoxState, boxes: boxes, modified: modified };
        }

        case CreateBoxActionType.ERROR: {
            const { error } = action;
            return { ...initialBoxState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

export const get = (state: BoxState = initialBoxState, action: GetBoxAction): BoxState => {
    switch (action.type) {
        case GetBoxActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
        }

        case GetBoxActionType.SUCCESS: {
            const { payload } = action;
            let { boxes } = state;

            if (payload) {
                boxes = replaceOrAppend(boxes, payload);
            }

            return { ...initialBoxState, boxes: boxes, modified: undefined };
        }

        case GetBoxActionType.ERROR: {
            const { error } = action;
            return { ...initialBoxState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

export const find = (state: BoxState = initialBoxState, action: FindBoxesAction): BoxState => {
    switch (action.type) {
        case FindBoxesActionType.LOADING: {
            const { loading } = action;
            return { ...state, loading: loading, modified: undefined };
        }

        case FindBoxesActionType.SUCCESS: {
            const { payload } = action;
            let { boxes } = state;
            if (payload) {
                payload.forEach(box => {
                    boxes = replaceOrAppend(boxes, box);
                });
            }
            return { ...initialBoxState, boxes: payload, modified: undefined };
        }

        case FindBoxesActionType.ERROR: {
            const { error } = action;
            return { ...initialBoxState, error: error, modified: undefined };
        }

        default: {
            return state;
        }
    }
}

const replaceOrAppend = (boxes: Box[], box: Box) => {
    const index = boxes.map(box => box.id).indexOf(box.id);

    if (~index) {
        boxes[index] = box;
    } else {
        boxes = boxes.concat(box);
    }

    return boxes;
}