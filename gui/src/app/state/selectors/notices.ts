import { Error, Notice, RootState, EntityType } from "../../models";

export const noticesSelector = (state: RootState) => {
    const notices: Notice[] = [];

    const errors = mapErrors(state);

    errors.forEach(error => {
        const { message } = error;
        notices.push({
            severity: 'error',
            header: 'Error',
            content: message
        });
    });

    return notices;
}

const mapErrors = (state: RootState): Error[] => {
    const errors: Error[] = [];
    if (state.groupState.error) {
        const { error } = state.groupState;
        errors.push({ ...error, entityType: EntityType.GROUP });
    }

    if (state.boxState.error) {
        const { error } = state.boxState;
        errors.push({ ...error, entityType: EntityType.BOX });
    }

    if (state.versionState.error) {
        const { error } = state.versionState;
        errors.push({ ...error, entityType: EntityType.VERSION });
    }

    if (state.providerState.error) {
        const { error } = state.providerState;
        errors.push({ ...error, entityType: EntityType.PROVIDER });
    }
    return errors;
}