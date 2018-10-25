import { GroupState, initialGroupState } from './groups';
import { BoxState, initialBoxState } from './boxes';

export interface RootState {
    groupState: GroupState;
    boxState: BoxState;
}

export const initialRootState: RootState = {
    groupState: initialGroupState,
    boxState: initialBoxState
};

export interface Error {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
}