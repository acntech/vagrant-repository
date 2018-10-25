import {
    BoxState,
    BoxAction,
    FindBoxesAction,
    FindBoxesActionType,
    initialBoxState
} from '../../models';

export function reducer(state: BoxState = initialBoxState, action: BoxAction): BoxState {
    switch (action.type) {
        case FindBoxesActionType.LOADING:
        case FindBoxesActionType.SUCCESS:
        case FindBoxesActionType.ERROR:
            return find(state, action);
        default:
            return state;
    }
}

export function find(state: BoxState = initialBoxState, action: FindBoxesAction): BoxState {
    switch (action.type) {
        case FindBoxesActionType.LOADING: {
            const {loading} = action;
            return {...state, loading: loading};
        }

        case FindBoxesActionType.SUCCESS: {
            const {payload} = action;
            let {boxes} = state;
            if (payload) {
                payload.forEach(box => {
                    let index = boxes.indexOf(box);
                    if (~index) {
                        boxes[index] = box;
                    } else {
                        boxes = boxes.concat(payload);
                    }
                });
            }
            return {...initialBoxState, boxes: payload};
        }

        case FindBoxesActionType.ERROR: {
            const {error} = action;
            return {...initialBoxState, error: error};
        }

        default: {
            return state;
        }
    }
}