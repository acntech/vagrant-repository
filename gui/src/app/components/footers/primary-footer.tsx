import { FunctionComponent } from 'react';
import * as React from 'react';
import { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import { Segment } from 'semantic-ui-react'
import { ManagementState, RootState } from '../../models/types';

interface ComponentStateProps {
    managementState: ManagementState;
}

interface ComponentDispatchProps {
}

type ComponentProps = ComponentDispatchProps & ComponentStateProps;

class PrimaryFooterComponent extends Component<ComponentProps> {

    public render(): ReactNode {
        const { info } = this.props.managementState;
        const { version } = info ? info.build : { version: 'UNKNOWN' };
        const { revision, revisionUrl } = info ? info.build.scm : { revision: 'UNKNOWN', revisionUrl: 'UNKNOWN' };

        return (
            <Segment basic className="primary-footer">
                <ApplicationDetailsFragment
                    version={version}
                    revision={revision}
                    revisionUrl={revisionUrl}/>
            </Segment>
        );
    }
}

interface ApplicationDetailsFragmentProps {
    version: string;
    revision: string;
    revisionUrl: string;
}

const ApplicationDetailsFragment: FunctionComponent<ApplicationDetailsFragmentProps> = (props: ApplicationDetailsFragmentProps) => {
    const { version, revision, revisionUrl } = props;
    const versionDetails = (<span>Version: {version}</span>);
    const revisionDetails = (<span>Revision: {revision === 'UNKNOWN' ? <>UNKNOWN</> : <a href={revisionUrl} target="_blank">{revision}</a>}</span>);

    return (
        <Segment basic className="application-details">
            <div>{versionDetails}</div>
            <div>{revisionDetails}</div>
        </Segment>
    );
};

const mapStateToProps = (state: RootState): ComponentStateProps => ({
    managementState: state.managementState
});

const mapDispatchToProps = (dispatch): ComponentDispatchProps => ({
});

const ConnectedPrimaryFooterComponent = connect(mapStateToProps, mapDispatchToProps)(PrimaryFooterComponent);

export { ConnectedPrimaryFooterComponent as PrimaryFooter };