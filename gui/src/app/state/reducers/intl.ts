import * as _ from 'lodash';

import * as initialState from '../store/initial-state';

export function reducer(state = initialState, action) {
    if (action.type === '@@intl/UPDATE') {
        const newState = _.merge({}, state);
        return _.merge(newState, action.payload);
    }

    return state;
}
