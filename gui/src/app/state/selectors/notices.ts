import { Error, Notice, RootState } from "../../models";

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
        const { data: error } = state.groupState.error.response;
        errors.push({ ...error, domain: 'group' });
    }

    if (state.boxState.error) {
        const { data: error } = state.boxState.error.response;
        errors.push({ ...error, domain: 'box' });
    }

    if (state.versionState.error) {
        const { data: error } = state.versionState.error.response;
        errors.push({ ...error, domain: 'version' });
    }

    if (state.providerState.error) {
        const { data: error } = state.providerState.error.response;
        errors.push({ ...error, domain: 'provider' });
    }
    return errors;
}