import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Header, Segment } from 'semantic-ui-react'

import { RootState } from '../../models';

interface ComponentStateProps {
}

interface ComponentDispatchProps {
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

        return (
            <Segment basic>
                <Header>{children ? children : title}</Header>
                {subtitle ? <Header.Subheader>{subtitle}</Header.Subheader> : null}
            </Segment>
        );
    }
}

const mapStateToProps = (state: RootState): ComponentStateProps => ({
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
});

const ConnectedSecondaryHeaderComponent = connect(mapStateToProps, mapDispatchToProps)(SecondaryHeaderComponent);

export { ConnectedSecondaryHeaderComponent as SecondaryHeader };