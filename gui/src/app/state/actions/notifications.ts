import {
    ClearNotificationsAction,
    DismissNotificationAction,
    NotificationActionType,
    ShowNotificationAction,
    ShowNotification
} from '../../models';

const showNotificationAction = (notification: ShowNotification): ShowNotificationAction => ({ type: NotificationActionType.SHOW, notification: notification });
const dismissNotificationAction = (uuid: string): DismissNotificationAction => ({ type: NotificationActionType.DISMISS, uuid: uuid });
const clearNotificationsAction = (): ClearNotificationsAction => ({ type: NotificationActionType.CLEAR });

export function showInfo(title: string, message?: string) {
    return showNotification({ severity: 'info', title: title, content: message });
}

export function showWarning(title: string, message?: string) {
    return showNotification({ severity: 'warning', title: title, content: message });
}

export function showError(title: string, message?: string) {
    return showNotification({ severity: 'error', title: title, content: message });
}

export function showSuccess(title: string, message?: string) {
    return showNotification({ severity: 'success', title: title, content: message });
}

export function showNotification(notification: ShowNotification) {
    return (dispatch) => {
        dispatch(showNotificationAction(notification));
    };
}

export function dismissNotification(uuid: string) {
    return (dispatch) => {
        dispatch(dismissNotificationAction(uuid));
    };
}

export function clearNotifications() {
    return (dispatch) => {
        dispatch(clearNotificationsAction());
    };
}