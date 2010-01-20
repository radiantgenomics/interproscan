/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.interpro.scan.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Set;

/**
 * HMMER3 match.
 *
 * @author  Antony Quinn
 * @version $Id$
 */
@Entity
@Table(name="hmmer3_match")
@XmlType(name="Hmmer3MatchType")
public class Hmmer3Match extends HmmerMatch<Hmmer3Match.Hmmer3Location> {

    protected Hmmer3Match() {}

    public Hmmer3Match(Signature signature, double score, double evalue, Set<Hmmer3Match.Hmmer3Location> locations) {
        super(signature, score, evalue, locations);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hmmer3Match)) return false;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(39, 59)
                .appendSuper(super.hashCode())
                .toHashCode();
    }

    /**
     * Location(s) of match on protein sequence
     *
     * @author  Antony Quinn
     */
    @Entity
    @Table(name="hmmer3_location")
    @XmlType(name="Hmmer3LocationType")
    public static class Hmmer3Location extends HmmerLocation {

        private int envelopeStart;
        private int envelopeEnd;
       
        /**
         * protected no-arg constructor required by JPA - DO NOT USE DIRECTLY.
         */
        protected Hmmer3Location() {}

        public Hmmer3Location(int start, int end, double score, double evalue,
                             int hmmStart, int hmmEnd, HmmBounds hmmBounds,
                             int envelopeStart, int envelopeEnd) {
            super(start, end, score, evalue, hmmStart, hmmEnd, hmmBounds);
            setEnvelopeStart(envelopeStart);
            setEnvelopeEnd(envelopeEnd);
        }

        @XmlAttribute(name="env-start", required=true)
        public int getEnvelopeStart() {
            return envelopeStart;
        }

        private void setEnvelopeStart(int envelopeStart) {
            this.envelopeStart = envelopeStart;
        }

        @XmlAttribute(name="env-end", required=true)
        public int getEnvelopeEnd() {
            return envelopeEnd;
        }

        private void setEnvelopeEnd(int envelopeEnd) {
            this.envelopeEnd = envelopeEnd;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Hmmer3Location)) return false;
            Hmmer3Location l = (Hmmer3Location)o;
            return new EqualsBuilder()
                    .appendSuper(super.equals(o))
                    .append(envelopeStart, l.envelopeStart)
                    .append(envelopeEnd, l.envelopeEnd)
                    .isEquals();
        }

        @Override public int hashCode() {
            return new HashCodeBuilder(39, 63)
                    .appendSuper(super.hashCode())
                    .append(envelopeStart)
                    .append(envelopeEnd)
                    .toHashCode();
        }
    }
}