import { GroupState, initialGroupState } from './groups';
import { BoxState, initialBoxState } from './boxes';
import { VersionState, initialVersionState } from './versions';

export interface RootState {
    groupState: GroupState;
    boxState: BoxState;
    versionState: VersionState;
}

export const initialRootState: RootState = {
    groupState: initialGroupState,
    boxState: initialBoxState,
    versionState: initialVersionState
};

export interface Error {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
}