import uuidv4 from 'uuid/v4';
import {
    ClearNotificationsAction,
    DismissNotificationAction,
    NotificationAction,
    NotificationState,
    NotificationActionType,
    ShowNotificationAction
} from '../../models';
import { initialNotificationState } from '../store/initial-state';

export const reducer = (state: NotificationState = initialNotificationState, action: NotificationAction): NotificationState => {
    switch (action.type) {
        case NotificationActionType.SHOW:
            return show(state, action);
        case NotificationActionType.DISMISS:
            return dismiss(state, action);
        case NotificationActionType.CLEAR:
            return clear(state, action);
        default:
            return state;
    }
};

const show = (state: NotificationState = initialNotificationState, action: ShowNotificationAction): NotificationState => {
    switch (action.type) {
        case NotificationActionType.SHOW: {
            let { notifications } = state;
            const { notification } = action;
            notifications = notifications.concat({ ...notification, uuid: uuidv4() });
            return { ...initialNotificationState, notifications: notifications };
        }

        default: {
            return state;
        }
    }
};

const dismiss = (state: NotificationState = initialNotificationState, action: DismissNotificationAction): NotificationState => {
    switch (action.type) {
        case NotificationActionType.DISMISS: {
            let { notifications } = state;
            const { uuid } = action;
            notifications = notifications.filter(notification => notification.uuid !== uuid);
            return { ...initialNotificationState, notifications: notifications };
        }

        default: {
            return state;
        }
    }
};

const clear = (state: NotificationState = initialNotificationState, action: ClearNotificationsAction): NotificationState => {
    switch (action.type) {
        case NotificationActionType.CLEAR: {
            return initialNotificationState;
        }

        default: {
            return state;
        }
    }
};