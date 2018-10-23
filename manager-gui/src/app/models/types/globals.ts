import { GroupState, initialGroupState } from './groups';

export interface RootState {
    groupState: GroupState;
}

export const initialRootState: RootState = {
    groupState: initialGroupState
};

export interface Error {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
}