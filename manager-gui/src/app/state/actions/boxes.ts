import axios from 'axios';

import {
    Box,
    FindBoxesActionType,
    FindBoxesErrorAction,
    FindBoxesLoadingAction,
    FindBoxesSuccessAction,
} from '../../models';

const findBoxesLoading = (loading: boolean): FindBoxesLoadingAction => ({type: FindBoxesActionType.LOADING, loading});
const findBoxesSuccess = (payload: Box[]): FindBoxesSuccessAction => ({type: FindBoxesActionType.SUCCESS, payload});
const findBoxesError = (error: any): FindBoxesErrorAction => ({type: FindBoxesActionType.ERROR, error});

const rootPath = '/api/groups';

export function findBoxes(id: number) {
    return (dispatch) => {
        dispatch(findBoxesLoading(true));
        const url = id ? `${rootPath}/${id}/boxes` : rootPath;
        return axios.get(url)
            .then((response) => {
                return dispatch(findBoxesSuccess(response.data));
            })
            .catch((error) => {
                return dispatch(findBoxesError(error));
            });
    };
}