import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Header, Icon, Segment } from 'semantic-ui-react'
import { Link } from 'react-router-dom';

interface ComponentProps {
    title: string;
    subtitle?: string;
}

class MainHeaderComponent extends Component<ComponentProps> {

    public render(): ReactNode {
        const title = this.processTitle();

        document.title = title;

        return (
            <Segment basic>
                <Header as='h1'>
                    <Link className="header-link" to="/">
                        <Icon name='cubes' />{title}
                    </Link>
                </Header>
            </Segment>
        );
    }

    private processTitle() {
        const { title, subtitle } = this.props;
        if (subtitle) {
            return `${subtitle} - ${title}`;
        } else {
            return `${title}`;
        }
    }
}

export { MainHeaderComponent as MainHeader };