import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Header, Segment, Message } from 'semantic-ui-react'

import { Notification, NotificationState, RootState } from '../../models';
import { clearNotifications, dismissNotification } from '../../state/actions';

const notificationDetails = {
    info: {
        icon: 'info circle',
        info: true,
        warning: false,
        error: false,
        success: false,
        timeout: 5000
    },
    warning: {
        icon: 'warning circle',
        info: false,
        warning: true,
        error: false,
        success: false,
        timeout: 2000
    },
    error: {
        icon: 'ban',
        info: false,
        warning: false,
        error: true,
        success: false,
        timeout: 5000
    },
    success: {
        icon: 'check circle',
        info: false,
        warning: false,
        error: false,
        success: true,
        timeout: 2000
    }
};

interface ComponentStateProps {
    notificationState: NotificationState;
}

interface ComponentDispatchProps {
    clearNotifications: () => Promise<any>;
    dismissNotification: (uuid: string) => Promise<any>;
}

interface ComponentFieldProps {
    title?: string;
    subtitle?: string;
    children?: string | ReactNode;
}

type ComponentProps = ComponentFieldProps & ComponentDispatchProps & ComponentStateProps;

class SecondaryHeaderComponent extends Component<ComponentProps> {

    public render(): ReactNode {
        const { title, subtitle, children } = this.props;
        const { notifications } = this.props.notificationState;

        return (
            <div>
                <HeaderFragment title={title} subtitle={subtitle} children={children} />
                <MessagesFragment notifications={notifications} onDismissMessage={this.onDismissMessage} />
            </div>
        );
    }

    private onDismissMessage = (uuid: string) => {
        this.props.dismissNotification(uuid);
    }
}

interface HeaderFragmentProps {
    title?: string;
    subtitle?: string;
    children?: string | ReactNode;
}

const HeaderFragment: React.SFC<HeaderFragmentProps> = (props) => {
    const { title, subtitle, children } = props;

    return (
        <Segment basic className="secondary-header">
            <Header>{children ? children : title}</Header>
            {subtitle ? <Header.Subheader>{subtitle}</Header.Subheader> : null}
        </Segment>
    );
};

interface MessagesFragmentProps {
    notifications: Notification[];
    onDismissMessage: (uuid: string) => void;
}

const MessagesFragment: React.SFC<MessagesFragmentProps> = (props) => {
    const { notifications, onDismissMessage } = props;

    if (notifications && notifications.length > 0) {
        return (
            <Segment basic className="secondary-header">
                {notifications.map((notification, index) => {
                    const { uuid, severity, title, content } = notification;
                    const {
                        icon,
                        info,
                        warning,
                        error,
                        success,
                        timeout
                    } = notificationDetails[severity];

                    window.setTimeout(() => onDismissMessage(uuid), timeout);

                    return <Message
                        key={index}
                        info={info}
                        warning={warning}
                        error={error}
                        success={success}
                        icon={icon}
                        header={title}
                        content={content}
                        onDismiss={() => onDismissMessage(uuid)} />;
                })}
            </Segment>
        );
    } else {
        return null;
    }
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    notificationState: state.notificationState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
    clearNotifications: () => dispatch(clearNotifications()),
    dismissNotification: (uuid: string) => dispatch(dismissNotification(uuid))
});

const ConnectedSecondaryHeaderComponent = connect(mapStateToProps, mapDispatchToProps)(SecondaryHeaderComponent);

export { ConnectedSecondaryHeaderComponent as SecondaryHeader };